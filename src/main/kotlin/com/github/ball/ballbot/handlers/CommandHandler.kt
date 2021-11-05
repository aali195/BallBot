package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.commands.Command
import com.github.ball.ballbot.commands.CommandContext
import com.github.ball.ballbot.commands.GuildPrefixCommand
import com.github.ball.ballbot.commands.MarkovCommand

object CommandHandler {

    internal val activeCommands: Set<Command> = setOf(
        MarkovCommand,
        GuildPrefixCommand
    )

    internal operator fun invoke(context: CommandContext) = activeCommands
        .find { it.command == context.commandString }
        ?.execute(context)

}
