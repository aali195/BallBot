package com.github.ball.ballbot.client

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.core.session.config.token
import blue.starry.penicillin.endpoints.timeline
import blue.starry.penicillin.endpoints.timeline.userTimelineByScreenName
import blue.starry.penicillin.models.Status

object TwitterClient {

    private val client = PenicillinClient {
        account {
            application(
                consumerKey = System.getenv("TWITTER_CONSUMER_KEY"),
                consumerSecret = System.getenv("TWITTER_CONSUMER_SECRET")
            )
            token(
                accessToken = System.getenv("TWITTER_ACCESS_TOKEN"),
                accessTokenSecret = System.getenv("TWITTER_ACCESS_TOKEN_SECRET")
            )
        }
    }

    internal suspend fun getLastTweetByUrlName(urlName: String, count: Int = 1): Set<Status> =
        client.timeline
            .userTimelineByScreenName(
                screenName = urlName,
                count = count,
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
