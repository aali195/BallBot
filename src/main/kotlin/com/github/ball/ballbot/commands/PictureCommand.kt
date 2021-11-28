package com.github.ball.ballbot.commands

import com.github.ball.ballbot.domain.generated.tables.records.PictureRecord
import com.github.ball.ballbot.repository.PictureRepository
import com.github.ball.ballbot.repository.PictureRepositoryImpl
import dev.minn.jda.ktx.Embed
import mu.KotlinLogging
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

object PictureCommand : Command() {

    private val pictureRepo: PictureRepository = PictureRepositoryImpl

    override val command: String = "p"

    override fun execute(context: CommandContext): Unit = with(context) {
        when (commandArgs.getOrNull(0)) {
            null -> message.reply("its: $usage").queue()
            "add" -> addSubCommand(this)
            "info" -> infoSubCommand(this)
            "delete" -> deleteSubCommand(this)
            "tag" -> tagSubCommand(this)
            else -> getUrlSubCommand(this)
        }
    }

    private fun addSubCommand(context: CommandContext) = with(context) {
        val name = commandArgs.getOrNull(1)
        val attachment = message.attachments.firstOrNull()
        val url = attachment?.url
            ?: commandArgs.getOrNull(2)?.takeIf { it.matches(URL_REGEX) }
        val tags = attachment?.let { commandArgs.drop(2) } // URl would not be passed for attachments
            ?: commandArgs.drop(3)

        if (name != null && url != null) {
            val result = pictureRepo.insert(
                name = name,
                urlName = url,
                guildId = guild.id,
                uploaderId = author.id,
                tags = tags.plus(name).toSet()
            )
            if (result == 1) reactWithComplete() else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun infoSubCommand(context: CommandContext) = with(context) {
        val pictureName = commandArgs.getOrNull(1)
        if (pictureName != null) {
            pictureRepo.getInfo(name = pictureName, guildId = guild.id)
                ?.run { message.reply(asInfoMessageEmbed(context)).queue() } //doesnt exist too
        } else message.reply("its: $usage").queue()
    }

    private fun deleteSubCommand(context: CommandContext) = with(context) {
        val name = commandArgs.getOrNull(1)
        if (name != null) {
            val result = if (member?.isOwner == true) {
                logger.warn { "admin deleting picture with name: $name from guildId: ${guild.id}" }
                pictureRepo.adminDelete(name = name, guildId = guild.id)
            } else {
                logger.warn { "deleting picture with name: $name from guildId: ${guild.id}" }
                pictureRepo.delete(name = name, uploaderId = author.id, guildId = guild.id)
            }

            if (result == 1) reactWithComplete() else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun tagSubCommand(context: CommandContext) = with(context) {
        val tags = commandArgs.drop(1)
        if (tags.isNotEmpty()) {
            pictureRepo.getUrlsByTag(tags = tags, guildId = guild.id)
                .takeIf { it.isNotEmpty() }
                ?.run { message.reply(this.random()!!).queue() }
                ?: reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun getUrlSubCommand(context: CommandContext) = with(context) {
        pictureRepo.getUrl(name = commandArgs[0], guildId = guild.id)
            ?.run { message.reply(this).queue() }
            ?: reactWithFail()
    }

    override val description: String = """
        Allows each server to have their own reaction sticker style pictures, each with their own tags
    """.trimIndent()

    override val usage: String = """
        
        add via url:
            `[prefix]$command add [name] [url] (optional tags with spaces between)`
        add via attachment:
            `[prefix]$command add [name] (optional tags with spaces between)`
        get info:
            `[prefix]$command info [name]`
        get url:
            `[prefix]$command [name]`
        delete (uploader and admins only):
            `[prefix]$command delete [name]`
        get randomly tagged:
            `[prefix]$command tag [tag]`
    """.trimIndent()

}

private val URL_REGEX = "(https?:\\/\\/).*\\.[a-zA-Z0-9]{2,6}\\/*.+".toRegex()

private fun PictureRecord.asInfoMessageEmbed(context: CommandContext) = Embed {
    color = 0xFFFFFF
    image = this@asInfoMessageEmbed.url!!
    field {
        name = "Name"
        value = this@asInfoMessageEmbed.name!!
        inline = false
    }
    field {
        name = "URL"
        value = this@asInfoMessageEmbed.url!!
        inline = false
    }
    field {
        name = "Created"
        value = this@asInfoMessageEmbed.created!!
            .format(DateTimeFormatter.RFC_1123_DATE_TIME)
        inline = false
    }
    field {
        name = "Uploader"
        value = context.jda.retrieveUserById(this@asInfoMessageEmbed.uploaderId!!)
            .complete().name
        inline = false
    }
    if (!tags.isNullOrEmpty()) {
        field {
            name = "Tags"
            value = this@asInfoMessageEmbed.tags.contentToString()
            inline = false
        }
    }
}
