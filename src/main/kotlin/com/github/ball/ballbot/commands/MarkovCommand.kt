package com.github.ball.ballbot.commands

object MarkovCommand : Command() {

    override val command: String = "markov"

    override fun execute(context: CommandContext) = with(context) {
        channel.sendTyping()
        message.reply("ded...").queue()
    }

    override val description: String = """
        Replies with the fate of our old friend `markov`
    """.trimIndent()

    override val usage: String = """
        `[prefix]markov`
    """.trimIndent()

}
