/*
 * This file is generated by jOOQ.
 */
package com.github.ball.ballbot.domain.generated


import com.github.ball.ballbot.domain.generated.tables.Guild

import kotlin.collections.List

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class BallbotSchema : SchemaImpl("ballbot_schema", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>ballbot_schema</code>
         */
        val BALLBOT_SCHEMA: BallbotSchema = BallbotSchema()
    }

    /**
     * The table <code>ballbot_schema.guild</code>.
     */
    val GUILD: Guild get() = Guild.GUILD

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        Guild.GUILD
    )
}