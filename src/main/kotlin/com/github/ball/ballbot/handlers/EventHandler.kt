package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.repository.GuildRepository
import com.github.ball.ballbot.repository.GuildRepositoryImpl
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object EventHandler : ListenerAdapter() {

    private val guildRepo: GuildRepository = GuildRepositoryImpl()

    private lateinit var prefixes: Map<String?, String?>

    override fun onReady(event: ReadyEvent) {
        updatePrefixMap()
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        guildRepo.createGuildEntry(event.guild.id, DEFAULT_COMMAND_PREFIX)
        updatePrefixMap()
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        val guildPrefix = prefixes[event.guild.id] ?: DEFAULT_COMMAND_PREFIX
        if (!event.message.contentRaw.startsWith(guildPrefix)) return

        val commandString = event.message.contentDisplay.removePrefix(guildPrefix)

        // TODO: Write an actual command handler
        if (commandString == "markov") {
            event.channel.sendTyping()
            event.message.reply("ded...").queue()
        }
        if (commandString.startsWith("prefix")) {
            val newPrefix = commandString.split(" ").getOrNull(1)
            if (newPrefix != null) {
                guildRepo.updateGuildPrefix(event.guild.id, newPrefix)
                event.message.reply("set server prefix: $newPrefix").queue()
                updatePrefixMap()
            } else {
                event.message.reply("its: `[current_prefix]prefix [new_prefix]`").queue()
            }
        }
    }

    private fun updatePrefixMap() {
        prefixes = guildRepo.getGuildIdToPrefixMap()
    }

}

private const val DEFAULT_COMMAND_PREFIX = "!"
