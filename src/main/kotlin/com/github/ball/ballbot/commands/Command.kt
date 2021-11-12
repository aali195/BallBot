package com.github.ball.ballbot.commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

abstract class Command {

    abstract val command: String

    abstract fun execute(context: CommandContext)

    abstract val description: String

    abstract val usage: String

    // Permission?

}

data class CommandContext(
    val channel: TextChannel,
    val message: Message,
    val guild: Guild,
    val member: Member?,
    val author: User,
    val prefix: String,
    val jda: JDA
) {
    private val messageArgs = message.contentDisplay.removePrefix(prefix).split(" ")
    val commandString: String = messageArgs[0]
    val commandArgs: List<String> = messageArgs.drop(1)

    fun reactWithComplete() = message.addReaction("✔").queue()
    fun reactWithFail() = message.addReaction("❌").queue()
}
