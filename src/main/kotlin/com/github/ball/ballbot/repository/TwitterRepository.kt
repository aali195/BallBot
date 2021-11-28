package com.github.ball.ballbot.repository

import com.github.ball.ballbot.domain.generated.tables.records.TwitterScheduleTaskRecord
import com.github.ball.ballbot.domain.generated.tables.references.TWITTER_SCHEDULE_TASK
import mu.KotlinLogging
import org.jooq.DSLContext

private val logger = KotlinLogging.logger {}

interface TwitterRepository {
    fun getTwitterTasks(): List<TwitterScheduleTaskRecord>
    fun insert(
        urlName: String,
        updateInterval: Long,
        description: String?,
        guildId: String,
        channelId: String,
        uploaderId: String
    ): Int?

    fun getInfo(urlName: String, guildId: String): TwitterScheduleTaskRecord?
    fun delete(urlName: String, guildId: String, uploaderId: String): Int?
    fun adminDelete(urlName: String, guildId: String): Int?
}

object TwitterRepositoryImpl : TwitterRepository {

    private val dslContext: DSLContext = DslConfig.dslContext

    override fun getTwitterTasks(): List<TwitterScheduleTaskRecord> = TWITTER_SCHEDULE_TASK
        .runCatching {
            dslContext
                .selectFrom(this)
                .fetch()
        }
        .onFailure { logger.error(it) { "failed to get twitter tasks" } }
        .onSuccess { logger.info { "twitter tasks fetched" } }
        .getOrThrow()

    override fun insert(
        urlName: String,
        updateInterval: Long,
        description: String?,
        guildId: String,
        channelId: String,
        uploaderId: String
    ): Int? = TWITTER_SCHEDULE_TASK
        .runCatching {
            dslContext
                .insertInto(this)
                .set(URL_NAME, urlName.lowercase())
                .set(UPDATE_INTERVAL, updateInterval)
                .apply { description?.let { set(DESCRIPTION, description) } }
                .set(GUILD_ID, guildId)
                .set(CHANNEL_ID, channelId)
                .set(UPLOADER_ID, uploaderId)
                .onConflictDoNothing()
                .execute()
        }
        .onFailure { logger.error(it) { "failed to create twitter task record with urlName: $urlName for guildId: $guildId" } }
        .getOrNull()

    override fun getInfo(urlName: String, guildId: String): TwitterScheduleTaskRecord? = TWITTER_SCHEDULE_TASK
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(URL_NAME.equalIgnoreCase(urlName), GUILD_ID.eq(guildId))
                .fetchOne()
        }
        .onFailure { logger.error(it) { "failed to find twitter tasks record with urlName: $urlName for guildId: $guildId" } }
        .getOrThrow()

    override fun delete(urlName: String, guildId: String, uploaderId: String): Int? = TWITTER_SCHEDULE_TASK
        .runCatching {
            dslContext
                .deleteFrom(this)
                .where(
                    URL_NAME.equalIgnoreCase(urlName),
                    GUILD_ID.eq(guildId),
                    UPLOADER_ID.eq(uploaderId)
                )
                .execute()
        }
        .onFailure { logger.error(it) { "failed to delete twitter task record with urlName: $urlName for guildId: $guildId and uploaderId: $uploaderId" } }
        .getOrNull()

    override fun adminDelete(urlName: String, guildId: String): Int? = TWITTER_SCHEDULE_TASK
        .runCatching {
            dslContext
                .deleteFrom(this)
                .where(
                    URL_NAME.equalIgnoreCase(urlName),
                    GUILD_ID.eq(guildId)
                )
                .execute()
        }
        .onFailure { logger.error(it) { "failed to admin delete twitter task record with urlName: $urlName for guildId: $guildId" } }
        .getOrNull()

}
