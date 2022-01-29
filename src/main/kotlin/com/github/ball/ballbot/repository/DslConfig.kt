package com.github.ball.ballbot.repository

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object DslConfig {

    val dslContext: DSLContext = HikariDataSource()
        .apply {
            jdbcUrl = System.getenv("DB_URL")
            username = System.getenv("DB_USER")
            password = System.getenv("DB_PASSWORD")
        }
        .let { DSL.using(it.connection, SQLDialect.POSTGRES) }

}

internal const val MAX_DISCORD_ALLOWED_ROWS = 25
