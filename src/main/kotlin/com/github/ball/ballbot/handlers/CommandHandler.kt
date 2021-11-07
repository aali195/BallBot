package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.commands.Command
import com.github.ball.ballbot.commands.CommandContext
import com.github.ball.ballbot.commands.GuildPrefixCommand
import com.github.ball.ballbot.commands.MarkovCommand
import com.github.ball.ballbot.commands.PictureCommand

object CommandHandler {

    internal val activeCommands: Set<Command> = setOf(
        MarkovCommand,
        GuildPrefixCommand,
        PictureCommand
    )

    internal operator fun invoke(context: CommandContext) = activeCommands
        .find { it.command == context.commandString }
        ?.execute(context)

}
