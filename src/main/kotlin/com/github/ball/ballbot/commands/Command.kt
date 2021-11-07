package com.github.ball.ballbot.commands

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User

abstract class Command {

    abstract val command: String

    abstract fun execute(context: CommandContext)

    open val description: String? = null

    open val usage: String? = null

    // Permission?

}

data class CommandContext(
    val channel: TextChannel,
    val message: Message,
    val guild: Guild,
    val member: Member?,
    val author: User,
    val prefix: String
) {
    private val messageArgs = message.contentDisplay.removePrefix(prefix).split(" ")
    val commandString: String = messageArgs[0]
    val commandArgs: List<String> = messageArgs.drop(1)
}
