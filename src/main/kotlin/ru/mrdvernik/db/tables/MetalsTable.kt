package ru.mrdvernik.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object MetalsTable: IntIdTable("metals") {
    val name = varchar("name", 250).uniqueIndex()
}
