package ru.mrdvernik.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
object DbConfig {
    fun setup(dbUrl: String, username: String, password: String){
        val config = HikariConfig().also { config ->
            config.driverClassName = "org.postgresql.Driver"
            config.jdbcUrl = dbUrl
            config.username = username
            config.password = password
            config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        Database.connect( HikariDataSource(config))
    }
}
