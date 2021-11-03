package com.github.ball.ballbot.repository

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.io.File
import java.io.FileInputStream
import java.util.*

object DslConfig {

    private val dbProperties = Properties().apply {
        load(FileInputStream(File("db.properties")))
    }

    val dslContext: DSLContext = HikariDataSource()
        .apply {
            jdbcUrl = dbProperties.getProperty("dbUrl")
            username = dbProperties.getProperty("dbUser")
            password = dbProperties.getProperty("dbPassword")
        }
        .let { DSL.using(it.connection, SQLDialect.POSTGRES) }

}
