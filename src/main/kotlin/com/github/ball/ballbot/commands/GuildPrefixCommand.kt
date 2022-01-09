package com.github.ball.ballbot.commands

import com.github.ball.ballbot.handlers.GuildEventHandler
import com.github.ball.ballbot.repository.GuildRepository
import com.github.ball.ballbot.repository.GuildRepositoryImpl

object GuildPrefixCommand : Command() {

    private val guildRepo: GuildRepository = GuildRepositoryImpl

    override val command: String = "prefix"

    override fun execute(context: CommandContext) = with(context) {
        commandArgs.getOrNull(0)
            .takeUnless { it.isNullOrEmpty() }
            ?.run {
                guildRepo.updateGuildPrefix(guild.id, this)
                reactWithComplete()
                GuildEventHandler.updatePrefixMap()
            }
            ?: run { message.reply("its: $usage").queue() }
    }

    override val description: String = """
        Updates the bot's prefix for this server
    """.trimIndent()

    override val usage: String = """
        `[current_prefix]$command [new_prefix]`
    """.trimIndent()

}
