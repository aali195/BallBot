package com.github.ball.ballbot

import com.github.ball.ballbot.handlers.EventHandler
import dev.minn.jda.ktx.light
import net.dv8tion.jda.api.entities.Activity
import java.io.File

fun main() {

    val token = File("token").readText().trim()

    light(token) {
        setActivity(Activity.watching("this server"))
        addEventListeners(EventHandler)
    }

}
