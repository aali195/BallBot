package com.github.ball.ballbot

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import java.io.File

private val TOKEN = File("src/main/resources/token").readText()

suspend fun main() {

    val api = Kord(TOKEN)

    api.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on
        if (message.content == "${COMMAND_PREFIX}markov") asPingPongCommand()
        return@on
    }

    api.login()

}

private const val COMMAND_PREFIX = "!"

private suspend fun MessageCreateEvent.asPingPongCommand() = message
    .channel
    .createMessage("ded :thinking:")
