package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.commands.Command
import com.github.ball.ballbot.commands.CommandContext
import com.github.ball.ballbot.commands.GuildPrefixCommand
import com.github.ball.ballbot.commands.MarkovCommand
import com.github.ball.ballbot.commands.PictureCommand
import dev.minn.jda.ktx.Embed

object CommandHandler {

    private val activeCommands: Set<Command> = setOf(
        MarkovCommand,
        GuildPrefixCommand,
        PictureCommand
    )

    internal operator fun invoke(context: CommandContext) = with(context) {
        if (commandString == "help") help(this)
        else activeCommands.find { it.command == commandString }?.execute(this)
    }

    private fun help(context: CommandContext) {
        context.message.reply(
            Embed {
                description = "The following are the available commands"
                color = 0xFFFFFF
                activeCommands.map {
                    field {
                        name = it.command
                        value = it.description
                        inline = false
                    }
                }
                footer { name = "Try them to see their usage." }
            }
        ).queue()
    }

}
