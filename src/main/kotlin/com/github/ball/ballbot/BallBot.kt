package com.github.ball.ballbot

import com.github.ball.ballbot.handlers.EventHandler
import com.github.ball.ballbot.scheduler.TaskScheduler
import dev.minn.jda.ktx.light
import net.dv8tion.jda.api.entities.Activity
import java.io.File

suspend fun main() {

    val token = File("token").readText().trim()

    val jda = light(token) {
        setActivity(Activity.watching("this server"))
        addEventListeners(EventHandler)
    }

    TaskScheduler.scheduleTweetRetrieval(jda)

}
