package com.github.ball.ballbot.commands

import com.github.ball.ballbot.handlers.EventHandler
import com.github.ball.ballbot.repository.GuildRepository
import com.github.ball.ballbot.repository.GuildRepositoryImpl

object GuildPrefixCommand : Command() {

    private val guildRepo: GuildRepository = GuildRepositoryImpl

    override val command: String = "prefix"

    override fun execute(context: CommandContext) = with(context) {
        val newPrefix = commandArgs.getOrNull(0)
        if (newPrefix != null) {
            guildRepo.updateGuildPrefix(guild.id, newPrefix)
            message.reply("set server prefix: $newPrefix").queue()
            EventHandler.updatePrefixMap()
        } else {
            message.reply("its: $usage").queue()
        }
    }

    override val description: String = """
        Updates the bot's prefix for this server
    """.trimIndent()

    override val usage: String = """
        `[current_prefix]prefix [new_prefix]`
    """.trimIndent()

}
