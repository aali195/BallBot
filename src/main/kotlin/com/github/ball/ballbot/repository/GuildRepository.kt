package com.github.ball.ballbot.repository

import com.github.ball.ballbot.domain.generated.tables.references.GUILD
import mu.KotlinLogging
import org.jooq.DSLContext
import java.time.OffsetDateTime
import java.time.ZoneOffset

private val logger = KotlinLogging.logger {}

interface GuildRepository {
    fun getGuildIdToPrefixMap(): Map<String?, String?>
    fun createGuild(guildId: String, defaultPrefix: String): Int
    fun updateGuildPrefix(guildId: String, updatedPrefix: String): Int
}

object GuildRepositoryImpl : GuildRepository {

    private val dslContext: DSLContext = DslConfig.dslContext

    override fun getGuildIdToPrefixMap(): Map<String?, String?> = GUILD
        .runCatching {
            dslContext
                .selectFrom(this)
                .fetchMap(ID, PREFIX)
                .toMap()
        }
        .onFailure { logger.error { "failed to get guild id to prefix map" } }
        .onSuccess { logger.info { "guild id to prefix map fetched" } }
        .getOrThrow()

    override fun createGuild(guildId: String, defaultPrefix: String): Int = GUILD
        .runCatching {
            dslContext
                .insertInto(this)
                .set(ID, guildId)
                .set(PREFIX, defaultPrefix)
                .onDuplicateKeyIgnore()
                .execute()
        }
        .onFailure { logger.error { "failed to create new guild record for guild id: $guildId" } }
        .onSuccess { logger.info { "new guild record created for guild id: $guildId" } }
        .getOrThrow()

    override fun updateGuildPrefix(guildId: String, updatedPrefix: String) = GUILD
        .runCatching {
            dslContext
                .update(this)
                .set(PREFIX, updatedPrefix)
                .set(LAST_UPDATED, OffsetDateTime.now(ZoneOffset.UTC))
                .where(ID.eq(guildId))
                .execute()
        }
        .onFailure { logger.error { "failed to update guild prefix for guild id: $guildId using: $updatedPrefix" } }
        .onSuccess { logger.info { "guild prefix updated for guild id: $guildId using: $updatedPrefix" } }
        .getOrThrow()

}
