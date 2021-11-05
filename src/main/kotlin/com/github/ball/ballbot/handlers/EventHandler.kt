package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.commands.CommandContext
import com.github.ball.ballbot.repository.GuildRepository
import com.github.ball.ballbot.repository.GuildRepositoryImpl
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object EventHandler : ListenerAdapter() {

    private val guildRepo: GuildRepository = GuildRepositoryImpl

    private lateinit var prefixes: Map<String?, String?>

    override fun onReady(event: ReadyEvent) {
        updatePrefixMap()
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        guildRepo.createGuildEntry(event.guild.id, DEFAULT_COMMAND_PREFIX)
        updatePrefixMap()
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        event.guild.id
            .let { prefixes[it] ?: DEFAULT_COMMAND_PREFIX }
            .takeUnless { event.isInvalid(it) }
            ?.run { CommandHandler(event.asCommandContext(this)) }
    }

    internal fun updatePrefixMap() {
        prefixes = guildRepo.getGuildIdToPrefixMap()
    }

}

private fun GuildMessageReceivedEvent.isInvalid(guildPrefix: String): Boolean =
    author.isBot && !message.contentRaw.startsWith(guildPrefix)

private fun GuildMessageReceivedEvent.asCommandContext(guildPrefix: String) = CommandContext(
    channel = channel,
    message = message,
    guild = guild,
    member = member,
    author = author,
    prefix = guildPrefix
)

private const val DEFAULT_COMMAND_PREFIX = "!"
