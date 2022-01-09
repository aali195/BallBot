package com.github.ball.ballbot.handlers

import com.github.ball.ballbot.commands.PICTURE_SELECTION_BUTTON_NEXT_ID_BASE
import com.github.ball.ballbot.commands.PICTURE_SELECTION_BUTTON_PREVIOUS_ID_BASE
import com.github.ball.ballbot.commands.PICTURE_SELECTION_ID_BASE
import com.github.ball.ballbot.commands.TWITTER_SELECTION_BUTTON_NEXT_ID_BASE
import com.github.ball.ballbot.commands.TWITTER_SELECTION_BUTTON_PREVIOUS_ID_BASE
import com.github.ball.ballbot.commands.TWITTER_SELECTION_ID_BASE
import com.github.ball.ballbot.commands.asInfoMessageEmbed
import com.github.ball.ballbot.repository.PictureRepository
import com.github.ball.ballbot.repository.PictureRepositoryImpl
import com.github.ball.ballbot.repository.TwitterRepository
import com.github.ball.ballbot.repository.TwitterRepositoryImpl
import dev.minn.jda.ktx.interactions.SelectionMenu
import dev.minn.jda.ktx.interactions.option
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

object InteractionEventHandler : ListenerAdapter() {

    private val pictureRepo: PictureRepository = PictureRepositoryImpl

    private val twitterRepo: TwitterRepository = TwitterRepositoryImpl

    override fun onSelectionMenu(event: SelectionMenuEvent) {
        event.selectionMenu?.id
            ?.run {
                when {
                    this.contains(Regex(PICTURE_SELECTION_ID_BASE)) -> onPictureSelection(event)
                    this.contains(Regex(TWITTER_SELECTION_ID_BASE)) -> onTwitterUrlNameSelection(event)
                }
            }
    }

    private fun onPictureSelection(event: SelectionMenuEvent) {
        val selectionId = event.selectionMenu!!.id!!
        if (selectionId.contains(Regex(PICTURE_SELECTION_ID_BASE))) {
            val picture = pictureRepo.getInfo(name = event.values.first(), guildId = event.guild!!.id)
            // ignores Discord API complaining (JDA exception) about only being able to respond to interactions
            // once after another command is called, this catch hides the interaction error the user would see as
            // if it was not a deferred edit
            runCatching { event.editMessageEmbeds(picture!!.asInfoMessageEmbed(event.jda)).queue() }
        }
    }

    private fun onTwitterUrlNameSelection(event: SelectionMenuEvent) {
        val selectionId = event.selectionMenu!!.id!!
        if (selectionId.contains(Regex(TWITTER_SELECTION_ID_BASE))) {
            val account = twitterRepo.getInfo(urlName = event.values.first(), guildId = event.guild!!.id)
            // ignores Discord API complaining (JDA exception) about only being able to respond to interactions
            // once after another command is called, this catch hides the interaction error the user would see as
            // if it was not a deferred edit
            runCatching { event.editMessageEmbeds(account!!.asInfoMessageEmbed(event.jda)).queue() }
        }
    }

    override fun onButtonClick(event: ButtonClickEvent) {
        event.button?.id
            ?.run {
                when {
                    this.contains(Regex(PICTURE_SELECTION_ID_BASE)) -> onPictureSelectionButton(event)
                    this.contains(Regex(TWITTER_SELECTION_ID_BASE)) -> onTwitterUrlNameSelectionButton(event)
                }
            }
    }

    private fun onPictureSelectionButton(event: ButtonClickEvent) {
        val buttonType = event.button!!.buttonType
        val firstItemValue = event.button!!.id!!.split("-")[1]
        val lastItemValue = event.button!!.id!!.split("-")[2]

        when (buttonType) {
            ButtonType.NEXT -> pictureRepo.getNextPageForGuild(guildId = event.guild!!.id, lastItemValue)
            ButtonType.PREVIOUS -> pictureRepo.getPreviousPageForGuild(guildId = event.guild!!.id, firstItemValue)
            else -> null
        }
            ?.takeIf { it.isNotEmpty() }
            ?.run {
                val newFirstItemValue = firstOrNull()?.name
                val newLastItemValue = lastOrNull()?.name

                val nextButton = Button.primary(
                    "$PICTURE_SELECTION_BUTTON_NEXT_ID_BASE-$newFirstItemValue-$newLastItemValue",
                    ">"
                )
                val previousButton = Button.primary(
                    "$PICTURE_SELECTION_BUTTON_PREVIOUS_ID_BASE-$newFirstItemValue-$newLastItemValue",
                    "<"
                )

                event.editMessage("Select a picture name or use the buttons to list more")
                    .setActionRows(
                        ActionRow.of(SelectionMenu("$PICTURE_SELECTION_ID_BASE-$newFirstItemValue-$newLastItemValue") {
                            forEach { option(it!!.name!!, it.name!!) }
                        }),
                        ActionRow.of(previousButton, nextButton)
                    )
                    .queue()
            }
            ?: event.interaction.editButton(Button.primary("disabled", "-").asDisabled()).queue()
    }

    private fun onTwitterUrlNameSelectionButton(event: ButtonClickEvent) {
        val buttonType = event.button!!.buttonType
        val firstItemValue = event.button!!.id!!.split("-")[1]
        val lastItemValue = event.button!!.id!!.split("-")[2]

        when (buttonType) {
            ButtonType.NEXT -> twitterRepo.getNextPageForGuild(guildId = event.guild!!.id, lastItemValue)
            ButtonType.PREVIOUS -> twitterRepo.getPreviousPageForGuild(guildId = event.guild!!.id, firstItemValue)
            else -> null
        }
            ?.takeIf { it.isNotEmpty() }
            ?.run {
                val newFirstItemValue = firstOrNull()?.urlName
                val newLastItemValue = lastOrNull()?.urlName

                val nextButton = Button.primary(
                    "$TWITTER_SELECTION_BUTTON_NEXT_ID_BASE-$newFirstItemValue-$newLastItemValue",
                    ">"
                )
                val previousButton = Button.primary(
                    "$TWITTER_SELECTION_BUTTON_PREVIOUS_ID_BASE-$newFirstItemValue-$newLastItemValue",
                    "<"
                )

                event.editMessage("Select an account url name or use the buttons to list more")
                    .setActionRows(
                        ActionRow.of(SelectionMenu("$TWITTER_SELECTION_ID_BASE-$newFirstItemValue-$newLastItemValue") {
                            forEach { option(it!!.urlName!!, it.urlName!!) }
                        }),
                        ActionRow.of(previousButton, nextButton)
                    )
                    .queue()
            }
            ?: event.interaction.editButton(Button.primary("disabled", "-").asDisabled()).queue()
    }

}

private enum class ButtonType { NEXT, PREVIOUS, UNKNOWN }

private val Button.buttonType: ButtonType
    get() {
        val buttonId = id!!.split("-").first()
        return when {
            buttonId.contains(Regex("Next")) -> ButtonType.NEXT
            buttonId.contains(Regex("Previous")) -> ButtonType.PREVIOUS
            else -> ButtonType.UNKNOWN
        }
    }
