package com.github.ball.ballbot.scheduler

import blue.starry.penicillin.core.exceptions.PenicillinTwitterApiException
import blue.starry.penicillin.models.Status
import com.github.ball.ballbot.client.TwitterClient
import com.github.ball.ballbot.domain.generated.tables.records.TwitterScheduleTaskRecord
import com.github.ball.ballbot.repository.TwitterRepository
import com.github.ball.ballbot.repository.TwitterRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA

private val logger = KotlinLogging.logger {}

object TwitterScheduler {

    private val twitterRepo: TwitterRepository = TwitterRepositoryImpl

    private val twitterClient = TwitterClient

    private var twitterTaskById: MutableMap<Long, TwitterScheduleTaskRecord> = mutableMapOf()

    internal suspend fun scheduleTweetRetrieval(jda: JDA) = supervisorScope { // call from a more general function
        updateTwitterTaskMap()
        jda.awaitReady()
        twitterTaskById.values
            .distinctBy { it.channelId }
            .forEach {
                jda.guildCache
                    .getElementById(it.guildId!!)
                    ?.getTextChannelById(it.channelId!!)
                    ?.sendMessage("⚠️ Bot has redeployed, (re)posting latest tweets ⚠️")
                    ?.queue()
            }

        val runningTwitterTaskJobById: MutableMap<Long, Job> = mutableMapOf()

        while (isActive) {
            runningTwitterTaskJobById.forEach { task ->
                if (!twitterTaskById.containsKey(task.key)) {
                    runningTwitterTaskJobById.remove(task.key)
                        .also { it!!.cancelAndJoin() }
                }
            }
            twitterTaskById.forEach { task ->
                if (!runningTwitterTaskJobById.containsKey(task.key)) {
                    runningTwitterTaskJobById[task.key] = runTwitterTask(jda, task)
                }
            }
            delay(60_000)
        }
    }

    private fun CoroutineScope.runTwitterTask(jda: JDA, tweetEntry: Map.Entry<Long, TwitterScheduleTaskRecord?>) =
        launch(Dispatchers.IO) {
            val urlName = tweetEntry.value!!.urlName!!
            val guildId = tweetEntry.value!!.guildId!!
            val channelId = tweetEntry.value!!.channelId!!
            val updateInterval = tweetEntry.value!!.updateInterval!!
            var lastPostId: Long? = null
            while (isActive) {
                try {
                    val tweets = lastPostId
                        ?.let { twitterClient.getTweetsByUrlNameSinceLastPostId(urlName, lastPostId!!) }
                        ?: twitterClient.getLastTweetByUrlName(urlName)
                    postOrDeleteIfNoChannel(tweets, jda, guildId, channelId)
                    tweets.firstOrNull()?.id?.run { lastPostId = this }
                } catch (e: PenicillinTwitterApiException) {
                    logger.warn { "problem with the Twitter API request: $e" }
                    jda.guildCache
                        .getElementById(guildId)
                        ?.getTextChannelById(channelId)
                        ?.run {
                            sendMessage(
                                "⚠️ There was a problem trying to find the Tweets of account @${tweetEntry.value!!.urlName}. " +
                                        "It may have been renamed, deleted or there is an issue with Twitter or this command. ⚠️"
                            ).queue()
                        }
                }
                delay(updateInterval)
            }
        }

    private fun postOrDeleteIfNoChannel(tweets: Set<Status>, jda: JDA, guildId: String, channelId: String) {
        val tweetsSortedByOldest = tweets.reversed()
        jda.guildCache
            .getElementById(guildId)
            ?.getTextChannelById(channelId)
            ?.run { tweetsSortedByOldest.forEach { sendMessage(it.asTwitterLinkWithStatus).queue() } }
            ?: run {
                logger.warn { "missing channel id: $channelId, deleting record" }
                twitterRepo.delete(tweets.first().user.screenName, guildId, channelId)
            }
    }

    internal fun updateTwitterTaskMap() {
        twitterTaskById = mutableMapOf()
        twitterRepo.getTwitterTasks().forEach { twitterTaskById[it.id!!] = it }
    }

}

private val Status.asTwitterLinkWithStatus: String
    get() = "https://twitter.com/${this.user.screenName}/status/${this.id}"
