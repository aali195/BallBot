package com.github.ball.ballbot.commands

import com.github.ball.ballbot.client.TwitterClient
import com.github.ball.ballbot.domain.generated.tables.records.TwitterScheduleTaskRecord
import com.github.ball.ballbot.repository.TwitterRepository
import com.github.ball.ballbot.repository.TwitterRepositoryImpl
import com.github.ball.ballbot.scheduler.TaskScheduler
import dev.minn.jda.ktx.Embed
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
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
            "add" -> addSubCommand(this)
            "info" -> infoSubCommand(this)
            "delete" -> deleteSubCommand(this)
            else -> message.reply("its: $usage").queue()
        }
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
                TaskScheduler.updateTwitterTaskMap()
                reactWithComplete()
            } else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun infoSubCommand(context: CommandContext) = with(context) {
        val urlName = commandArgs.getOrNull(1)
        if (urlName != null) {
            twitterRepo.getInfo(urlName = urlName, guildId = guild.id)
                ?.run { message.replyEmbeds(asInfoMessageEmbed(context)).queue() }
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
                TaskScheduler.updateTwitterTaskMap()
                reactWithComplete()
            } else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    override val description: String = """
        Retrieves the latest tweets by a Twitter account to create a common timeline feed for the server. 
        Best added to a separate channel made entirely to function as that timeline.
    """.trimIndent()

    override val usage: String = """
        
        add:
            `[prefix]$command add [name in URL] [timing in hours (0.5 minimum)] [optional description (spaces allowed)]`
        get info:
            `[prefix]$command info [name in URL]`
        delete (uploader and admins only):
            `[prefix]$command delete [name in URL]`
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

private val URL_NAME_REGEX = "^[a-zA-Z0-9_]+\$".toRegex()

private fun TwitterScheduleTaskRecord.asInfoMessageEmbed(context: CommandContext) = Embed {
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
        value = context.jda
            .retrieveUserById(this@asInfoMessageEmbed.uploaderId!!).complete().name
        inline = false
    }
    field {
        name = "Channel"
        value = context.jda
            .getGuildById(guildId!!)
            ?.getGuildChannelById(this@asInfoMessageEmbed.channelId!!)?.name
            ?: "Channel can't be found"
        inline = false
    }
}
