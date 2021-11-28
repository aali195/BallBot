package com.github.ball.ballbot.commands

object ChooseCommand : Command() {

    override val command: String = "choose"

    override fun execute(context: CommandContext): Unit = with(context) {
        message.contentDisplay
            .removePrefix("$prefix$command")
            .trimIndent()
            .takeIf { it.isNotEmpty() }
            ?.split(DELIMITER)
            ?.run { message.reply(random()).queue() }
            ?: run { message.reply("its: $usage").queue() }
    }

    override val description: String = """
        Chooses one option randomly from the provided items
    """.trimIndent()

    override val usage: String = """
        `[prefix]$command [items with ',' between (spaces allowed)]`
    """.trimIndent()

}

private const val DELIMITER = ","
