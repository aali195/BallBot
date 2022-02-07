package com.github.ball.ballbot.commands

import com.github.ball.ballbot.client.TwitterClient
import com.github.ball.ballbot.domain.generated.tables.records.TwitterScheduleTaskRecord
import com.github.ball.ballbot.repository.MAX_DISCORD_ALLOWED_ROWS
import com.github.ball.ballbot.repository.TwitterRepository
import com.github.ball.ballbot.repository.TwitterRepositoryImpl
import com.github.ball.ballbot.scheduler.TwitterScheduler
import com.github.ball.ballbot.scheduler.asTwitterLinkWithStatus
import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.interactions.SelectionMenu
import dev.minn.jda.ktx.interactions.option
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import java.time.format.DateTimeFormatter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val logger = KotlinLogging.logger {}

object TwitterCommand : Command() {

    private val twitterRepo: TwitterRepository = TwitterRepositoryImpl

    private val twitterClient = TwitterClient

    override val command: String = "twitter"

    override fun execute(context: CommandContext): Unit = with(context) {
        when (commandArgs.getOrNull(0)) {
            "get" -> getSubCommand(this)
            "add" -> addSubCommand(this)
            "info" -> infoSubCommand(this)
            "delete" -> deleteSubCommand(this)
            "all" -> allSubCommand(this)
            else -> message.reply("its: $usage").queue()
        }
    }

    private fun getSubCommand(context: CommandContext) = with(context) {
        val urlName = commandArgs.getOrNull(1)
        val count = commandArgs.getOrNull(2)?.toInt() ?: 1

        if (urlName != null) {
            runBlocking {
                runCatching { twitterClient.getLastTweetByUrlName(urlName, count) }
                    .onSuccess { tweets ->
                        message.reply(tweets.joinToString(separator = "\n") { it.asTwitterLinkWithStatus }).queue()
                    }
                    .onFailure {
                        logger.warn { it.message }
                        reactWithFail()
                    }
            }
        } else message.reply("its: $usage").queue()
    }

    private fun addSubCommand(context: CommandContext) = with(context) {
        val urlName = commandArgs.getOrNull(1)
            ?.takeIf { it.matches(URL_NAME_REGEX) }
            ?.takeIf { it.isExistingTwitterUrlName() }
        val updateInterval = commandArgs.getOrNull(2)
            ?.toDoubleOrNull()
            ?.takeIf { it >= 0.5 }
            ?.toDuration(DurationUnit.HOURS)
        val description = commandArgs.drop(3).joinToString(" ")

        if (urlName != null && updateInterval != null) {
            val result = twitterRepo.insert(
                urlName = urlName,
                description = description,
                updateInterval = updateInterval.inWholeMilliseconds,
                guildId = guild.id,
                channelId = channel.id,
                uploaderId = author.id,
            )
            if (result == 1) {
                TwitterScheduler.updateTwitterTaskMap()
                reactWithComplete()
            } else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun infoSubCommand(context: CommandContext) = with(context) {
        val urlName = commandArgs.getOrNull(1)
        if (urlName != null) {
            twitterRepo.getInfo(urlName = urlName, guildId = guild.id)
                ?.run { message.replyEmbeds(asInfoMessageEmbed(context.jda)).queue() }
                ?: reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun deleteSubCommand(context: CommandContext) = with(context) {
        val urlName = commandArgs.getOrNull(1)
        if (urlName != null) {
            val result = if (member?.isOwner == true) {
                logger.warn { "admin deleting twitter task with urlName: $urlName from guildId: ${guild.id}" }
                twitterRepo.adminDelete(urlName = urlName, guildId = guild.id)
            } else {
                logger.warn { "deleting twitter task with urlName: $urlName from guildId: ${guild.id}" }
                twitterRepo.delete(urlName = urlName, guildId = guild.id, uploaderId = author.id)
            }

            if (result == 1) {
                TwitterScheduler.updateTwitterTaskMap()
                reactWithComplete()
            } else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun allSubCommand(context: CommandContext) = with(context) {
        val firstPageAccounts = twitterRepo.getFirstPageForGuild(guildId = guild.id)
        val firstItemValue = firstPageAccounts.firstOrNull()?.urlName
        val lastItemValue = firstPageAccounts.lastOrNull()?.urlName

        if (firstPageAccounts.isNotEmpty()) {
            val nextButton = if (firstPageAccounts.size < MAX_DISCORD_ALLOWED_ROWS)
                Button.primary("Next-disabled", "-").asDisabled()
            else Button.primary("$TWITTER_SELECTION_BUTTON_NEXT_ID_BASE-$firstItemValue-$lastItemValue", ">")

            message.reply("Select an account url name to view its information or use the buttons to list more")
                .setActionRows(
                    ActionRow.of(SelectionMenu("$TWITTER_SELECTION_ID_BASE-$firstItemValue-$lastItemValue") {
                        firstPageAccounts.forEach { option(it.urlName!!, it.urlName!!) }
                    }),
                    ActionRow.of(
                        Button.primary("Previous-disabled", "-").asDisabled(),
                        nextButton
                    )
                )
                .queue()
        } else reactWithFail()
    }

    override val description: String = """
        Retrieves the latest tweets by a Twitter account to create a common timeline feed for the server. 
        Best added to a separate channel made entirely to function as that timeline.
    """.trimIndent()

    override val usage: String = """
        
        add:
            `[prefix]$command add [name in URL] [timing in hours (0.5 minimum)] [optional description (spaces allowed)]`
        get latest posts (count includes latest retweets which are excluded, with 1 as default):
            `[prefix]$command get [name in URL] (optional count number)`
        get info:
            `[prefix]$command info [name in URL]`
        delete (uploader and admins only):
            `[prefix]$command delete [name in URL]`        
        get info for all guild twitter account follows (embedded and via drop down list):
            `[prefix]$command all`
    """.trimIndent()

    private fun String.isExistingTwitterUrlName(): Boolean = runBlocking {
        try {
            twitterClient.getLastTweetByUrlName(this@isExistingTwitterUrlName)
            true
        } catch (e: Exception) {
            false
        }
    }

}

internal val TWITTER_SELECTION_ID_BASE = "${TwitterCommand.command}AllMenu"
internal val TWITTER_SELECTION_BUTTON_PREVIOUS_ID_BASE = "${TWITTER_SELECTION_ID_BASE}Previous"
internal val TWITTER_SELECTION_BUTTON_NEXT_ID_BASE = "${TWITTER_SELECTION_ID_BASE}Next"

private val URL_NAME_REGEX = "^[a-zA-Z0-9_]+\$".toRegex()

internal fun TwitterScheduleTaskRecord.asInfoMessageEmbed(jda: JDA) = Embed {
    color = 0xFFFFFF
    field {
        name = "Url Name"
        value = "https://twitter.com/${this@asInfoMessageEmbed.urlName!!}"
        inline = false
    }
    field {
        name = "Update Interval"
        value = this@asInfoMessageEmbed.updateInterval!!
            .toDuration(DurationUnit.MILLISECONDS).toString()
        inline = false
    }
    field {
        name = "Description"
        value = this@asInfoMessageEmbed.description ?: ""
        inline = false
    }
    field {
        name = "Added"
        value = this@asInfoMessageEmbed.added!!
            .format(DateTimeFormatter.RFC_1123_DATE_TIME)
        inline = false
    }
    field {
        name = "Uploader"
        value = jda.retrieveUserById(this@asInfoMessageEmbed.uploaderId!!).complete().name
        inline = false
    }
    field {
        name = "Channel"
        value = jda
            .getGuildById(guildId!!)
            ?.getGuildChannelById(this@asInfoMessageEmbed.channelId!!)?.name
            ?: "Channel can't be found"
        inline = false
    }
}
