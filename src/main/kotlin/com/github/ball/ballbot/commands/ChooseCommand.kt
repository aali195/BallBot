package com.github.ball.ballbot.commands

object ChooseCommand : Command() {

    override val command: String = "choose"

    override fun execute(context: CommandContext): Unit = with(context) {
        commandArgs
            .takeIf { it.isNotEmpty() }
            ?.run { message.reply(random()).queue() }
            ?: run { message.reply("its: $usage").queue() }
    }

    override val description: String = """
        Chooses one option randomly from the provided items
    """.trimIndent()

    override val usage: String = """
        `[prefix]choose [items with spaces between]`
    """.trimIndent()

}
