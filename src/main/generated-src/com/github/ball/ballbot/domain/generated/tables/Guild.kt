/*
 * This file is generated by jOOQ.
 */
package com.github.ball.ballbot.domain.generated.tables


import com.github.ball.ballbot.domain.generated.BallbotSchema
import com.github.ball.ballbot.domain.generated.indexes.GUILD_ID_IDX
import com.github.ball.ballbot.domain.generated.keys.GUILD_PKEY
import com.github.ball.ballbot.domain.generated.tables.records.GuildRecord

import java.time.OffsetDateTime

import kotlin.collections.List

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
import org.jooq.Name
import org.jooq.Record
import org.jooq.Row4
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Guild(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, GuildRecord>?,
    aliased: Table<GuildRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<GuildRecord>(
    alias,
    BallbotSchema.BALLBOT_SCHEMA,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>ballbot_schema.guild</code>
         */
        val GUILD: Guild = Guild()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<GuildRecord> = GuildRecord::class.java

    /**
     * The column <code>ballbot_schema.guild.id</code>.
     */
    val ID: TableField<GuildRecord, String?> = createField(DSL.name("id"), SQLDataType.CLOB.nullable(false), this, "")

    /**
     * The column <code>ballbot_schema.guild.joined</code>.
     */
    val JOINED: TableField<GuildRecord, OffsetDateTime?> = createField(DSL.name("joined"), SQLDataType.TIMESTAMPWITHTIMEZONE(6).defaultValue(DSL.field("now()", SQLDataType.TIMESTAMPWITHTIMEZONE)), this, "")

    /**
     * The column <code>ballbot_schema.guild.last_updated</code>.
     */
    val LAST_UPDATED: TableField<GuildRecord, OffsetDateTime?> = createField(DSL.name("last_updated"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "")

    /**
     * The column <code>ballbot_schema.guild.prefix</code>.
     */
    val PREFIX: TableField<GuildRecord, String?> = createField(DSL.name("prefix"), SQLDataType.CLOB.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<GuildRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<GuildRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>ballbot_schema.guild</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>ballbot_schema.guild</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>ballbot_schema.guild</code> table reference
     */
    constructor(): this(DSL.name("guild"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, GuildRecord>): this(Internal.createPathAlias(child, key), child, key, GUILD, null)
    override fun getSchema(): Schema? = if (aliased()) null else BallbotSchema.BALLBOT_SCHEMA
    override fun getIndexes(): List<Index> = listOf(GUILD_ID_IDX)
    override fun getPrimaryKey(): UniqueKey<GuildRecord> = GUILD_PKEY
    override fun `as`(alias: String): Guild = Guild(DSL.name(alias), this)
    override fun `as`(alias: Name): Guild = Guild(alias, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Guild = Guild(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Guild = Guild(name, null)

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------
    override fun fieldsRow(): Row4<String?, OffsetDateTime?, OffsetDateTime?, String?> = super.fieldsRow() as Row4<String?, OffsetDateTime?, OffsetDateTime?, String?>
}
