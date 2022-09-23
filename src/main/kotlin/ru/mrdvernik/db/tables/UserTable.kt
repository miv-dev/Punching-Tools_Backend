package ru.mrdvernik.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column

internal object UserTable : UUIDTable("user_table") {
    val email: Column<String> = varchar("email", 200).uniqueIndex()
    val username: Column<String> = varchar("username", 200).uniqueIndex()
    val password: Column<String> = varchar("password", 150)
    val isAdmin: Column<Boolean> = bool("isAdmin").default(false)
}
