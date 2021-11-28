package com.github.ball.ballbot.client

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.core.session.config.token
import blue.starry.penicillin.endpoints.timeline
import blue.starry.penicillin.endpoints.timeline.userTimelineByScreenName
import blue.starry.penicillin.models.Status
import java.io.File
import java.io.FileInputStream
import java.util.*

object TwitterClient {

    private val twitterProperties = Properties().apply {
        load(FileInputStream(File("twitter.properties")))
    }

    private val client = PenicillinClient {
        account {
            application(
                consumerKey = twitterProperties.getProperty("consumerKey"),
                consumerSecret = twitterProperties.getProperty("consumerSecret")
            )
            token(
                accessToken = twitterProperties.getProperty("accessToken"),
                accessTokenSecret = twitterProperties.getProperty("accessTokenSecret")
            )
        }
    }

    internal suspend fun getLastTweetByUrlName(urlName: String): Set<Status> =
        client.timeline
            .userTimelineByScreenName(
                screenName = urlName,
                count = 1,
                includeRTs = false,
                excludeReplies = true
            )
            .execute()
            .toSet()


    internal suspend fun getTweetsByUrlNameSinceLastPostId(urlName: String, lastPostId: Long): Set<Status> =
        client.timeline
            .userTimelineByScreenName(
                screenName = urlName,
                sinceId = lastPostId,
                includeRTs = false,
                excludeReplies = true
            )
            .execute()
            .toSet()

}
