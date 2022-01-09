package com.github.ball.ballbot.repository

import com.github.ball.ballbot.domain.generated.tables.records.PictureRecord
import com.github.ball.ballbot.domain.generated.tables.references.PICTURE
import mu.KotlinLogging
import org.jooq.DSLContext

private val logger = KotlinLogging.logger {}

interface PictureRepository {
    fun insert(name: String, guildId: String, uploaderId: String, urlName: String, tags: Set<String>): Int?
    fun getInfo(name: String, guildId: String): PictureRecord?
    fun getUrl(name: String, guildId: String): String?
    fun delete(name: String, guildId: String, uploaderId: String): Int?
    fun adminDelete(name: String, guildId: String): Int?
    fun getUrlsByTag(tags: List<String>, guildId: String): List<String?>
    fun getFirstPageForGuild(guildId: String): Set<PictureRecord>
    fun getPreviousPageForGuild(guildId: String, firstPictureName: String): Set<PictureRecord?>
    fun getNextPageForGuild(guildId: String, lastPictureName: String): Set<PictureRecord?>
}

object PictureRepositoryImpl : PictureRepository {

    private val dslContext: DSLContext = DslConfig.dslContext

    override fun insert(
        name: String,
        guildId: String,
        uploaderId: String,
        urlName: String,
        tags: Set<String>
    ): Int? = PICTURE
        .runCatching {
            dslContext
                .insertInto(this)
                .set(NAME, name)
                .set(GUILD_ID, guildId)
                .set(UPLOADER_ID, uploaderId)
                .set(URL, urlName.lowercase())
                .set(TAGS, tags.map { it.lowercase() }.toTypedArray())
                .onConflictDoNothing()
                .execute()
        }
        .onFailure { logger.error(it) { "failed to create picture record with name: $name in guild id: $guildId by uploader id: $uploaderId" } }
        .getOrNull()

    override fun getInfo(name: String, guildId: String): PictureRecord? = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(
                    NAME.equalIgnoreCase(name),
                    GUILD_ID.eq(guildId)
                )
                .fetchOne()
        }
        .onFailure { logger.error(it) { "failed to find picture record with name: $name for guild id: $guildId" } }
        .getOrThrow()

    override fun getUrl(name: String, guildId: String): String? = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(
                    NAME.equalIgnoreCase(name),
                    GUILD_ID.eq(guildId)
                )
                .fetchOne(URL)
        }
        .onFailure { logger.error(it) { "failed to find picture url with name: $name for guild id: $guildId" } }
        .getOrThrow()

    override fun delete(name: String, guildId: String, uploaderId: String): Int? = PICTURE
        .runCatching {
            dslContext
                .deleteFrom(this)
                .where(
                    NAME.equalIgnoreCase(name),
                    GUILD_ID.eq(guildId),
                    UPLOADER_ID.eq(uploaderId)
                )
                .execute()
        }
        .onFailure { logger.error(it) { "failed to delete picture record with name: $name by uploader id: $uploaderId" } }
        .getOrNull()

    override fun adminDelete(name: String, guildId: String): Int? = PICTURE
        .runCatching {
            dslContext
                .deleteFrom(this)
                .where(
                    NAME.equalIgnoreCase(name),
                    GUILD_ID.eq(guildId)
                )
                .execute()
        }
        .onFailure { logger.error(it) { "failed to admin delete picture record with name: $name for guild id: $guildId" } }
        .getOrNull()

    override fun getUrlsByTag(tags: List<String>, guildId: String): List<String?> = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(
                    TAGS.contains(tags.map { it.lowercase() }.toTypedArray()),
                    GUILD_ID.eq(guildId)
                )
                .fetch(URL)
                .toList()
        }
        .onFailure { logger.error(it) { "failed to get picture urls by tag with tags: $tags and guild id: $guildId" } }
        .getOrThrow()

    // using keyset pagination
    override fun getFirstPageForGuild(guildId: String): Set<PictureRecord> = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(GUILD_ID.eq(guildId))
                .orderBy(NAME)
                .limit(MAX_DISCORD_ALLOWED_ROWS)
                .fetch()
                .toSet()
        }
        .onFailure { logger.error(it) { "failed to get first page of picture records for guild id: $guildId" } }
        .getOrThrow()

    // using keyset pagination
    override fun getPreviousPageForGuild(guildId: String, firstPictureName: String): Set<PictureRecord?> = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(GUILD_ID.eq(guildId))
                .orderBy(NAME.desc()) //seekBefore is broken
                .seek(firstPictureName) //seekBefore is broken
                .limit(MAX_DISCORD_ALLOWED_ROWS)
                .fetch()
                .reversed() //seekBefore is broken
                .toSet()
        }
        .onFailure { logger.error(it) { "failed to get page before id: $firstPictureName of picture records for guild id: $guildId" } }
        .getOrThrow()

    // using keyset pagination
    override fun getNextPageForGuild(guildId: String, lastPictureName: String): Set<PictureRecord?> = PICTURE
        .runCatching {
            dslContext
                .selectFrom(this)
                .where(GUILD_ID.eq(guildId))
                .orderBy(NAME)
                .seekAfter(lastPictureName)
                .limit(MAX_DISCORD_ALLOWED_ROWS)
                .fetch()
                .toSet()
        }
        .onFailure { logger.error(it) { "failed to get page after id: $lastPictureName of picture records for guild id: $guildId" } }
        .getOrThrow()

}
