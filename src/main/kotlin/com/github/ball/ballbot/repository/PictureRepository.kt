package com.github.ball.ballbot.repository

import com.github.ball.ballbot.domain.generated.tables.records.PictureRecord
import com.github.ball.ballbot.domain.generated.tables.references.PICTURE
import mu.KotlinLogging
import org.jooq.DSLContext

private val logger = KotlinLogging.logger {}

interface PictureRepository {
    fun insertPicture(name: String, guildId: String, uploaderId: String, url: String, tags: Set<String>): Int?
    fun getPictureInfo(name: String, guildId: String): PictureRecord?
    fun getPictureUrl(name: String, guildId: String): String?
    fun deletePicture(name: String, uploaderId: String): Int?
    fun getPictureUrlsByTag(tags: List<String>, guildId: String): List<String?>
}

object PictureRepositoryImpl : PictureRepository {

    private val dslContext: DSLContext = DslConfig.dslContext

    override fun insertPicture(
        name: String,
        guildId: String,
        uploaderId: String,
        url: String,
        tags: Set<String>
    ): Int? = PICTURE
        .runCatching {
            dslContext
                .insertInto(this)
                .set(NAME, name)
                .set(GUILD_ID, guildId)
                .set(UPLOADER_ID, uploaderId)
                .set(URL, url)
                .set(TAGS, tags.toTypedArray())
                .execute()
        }
        .onFailure { logger.error { "failed to create picture record for name: $name guild id: $guildId" } }
        .getOrNull()

    override fun getPictureInfo(name: String, guildId: String): PictureRecord? = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(NAME.equalIgnoreCase(name), GUILD_ID.eq(guildId))
                .fetchOne()
        }
        .onFailure { logger.error { "failed to find picture record with name: $name for guild id: $guildId" } }
        .getOrThrow()

    override fun getPictureUrl(name: String, guildId: String): String? = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(NAME.equalIgnoreCase(name), GUILD_ID.eq(guildId))
                .fetchOne(URL)
        }
        .onFailure { logger.error { "failed to find picture url with name: $name for guild id: $guildId" } }
        .getOrThrow()

    override fun deletePicture(name: String, uploaderId: String): Int? = PICTURE
        .runCatching {
            dslContext
                .deleteFrom(this)
                .where(NAME.equalIgnoreCase(name), UPLOADER_ID.eq(uploaderId))
                .execute()
        }
        .onFailure { logger.error { "failed to delete picture record with name: $name and uploader id: $uploaderId" } }
        .getOrNull()

    override fun getPictureUrlsByTag(tags: List<String>, guildId: String): List<String?> = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(TAGS.contains(tags.toTypedArray()), GUILD_ID.eq(guildId))
                .fetch(URL)
                .toList()
        }
        .onFailure { logger.error { "failed to get picture urls by tag with tags: $tags and guild id: $guildId" } }
        .getOrThrow()
}
