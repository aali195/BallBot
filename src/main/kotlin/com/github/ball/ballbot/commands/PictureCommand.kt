package com.github.ball.ballbot.commands

import com.github.ball.ballbot.repository.PictureRepository
import com.github.ball.ballbot.repository.PictureRepositoryImpl

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
        val url = commandArgs.getOrNull(2)
            ?.takeIf { it.matches(URL_REGEX) }
        val tags = commandArgs.drop(3)
        if (name != null && url != null) {
            val result = pictureRepo.insertPicture(
                name = name,
                url = url,
                guildId = guild.id,
                uploaderId = author.id,
                tags = tags
            )
            if (result == 1) reactWithComplete() else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun infoSubCommand(context: CommandContext) = with(context) {
        message.reply("wip").queue()
    }

    private fun deleteSubCommand(context: CommandContext) = with(context) {
        val name = commandArgs.getOrNull(1)
        if (name != null) {
            val result = pictureRepo.deletePicture(name = name, uploaderId = author.id)
            if (result == 1) reactWithComplete() else reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun tagSubCommand(context: CommandContext) = with(context) {
        val tags = commandArgs.drop(1)
        if (tags.isNotEmpty()) {
            pictureRepo.getPictureUrlsByTag(tags = tags, guildId = guild.id)
                .takeIf { it.isNotEmpty() }
                ?.run { message.reply(this.random()!!).queue() }
                ?: reactWithFail()
        } else message.reply("its: $usage").queue()
    }

    private fun getUrlSubCommand(context: CommandContext) = with(context) {
        pictureRepo.getPictureUrl(name = commandArgs[0], guildId = guild.id)
            ?.run { message.reply(this).queue() }
            ?: reactWithFail()
    }

    override val description: String = """
        Allows each server to have their own reaction sticker style pictures, each with their own tags
    """.trimIndent()

    override val usage: String = """
        
        add:
            `[current_prefix]p add [name] [url] (optional tags with spaces between)`
        get info:
            `wip`
        get url:
            `[current_prefix]p [name]`
        delete (uploader only):
            `[current_prefix]p delete [name]`
        get randomly tagged:
            `[current_prefix]p tag [tag]`
    """.trimIndent()

}

private val URL_REGEX = "(https?:\\/\\/).*\\.[a-zA-Z0-9]{2,6}\\/*.+".toRegex()
