package com.github.ball.ballbot

import dev.minn.jda.ktx.await
import dev.minn.jda.ktx.light
import dev.minn.jda.ktx.listener
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.File

private val TOKEN = File("src/main/resources/token").readText()

fun main() {

    val jda = light(TOKEN)

    jda.listener<MessageReceivedEvent> {
        if (it.message.contentDisplay == "${COMMAND_PREFIX}markov") {
            it.channel.sendTyping().await()
            it.message.reply("ded...").queue()
        }
    }

}

private const val COMMAND_PREFIX = "!"
