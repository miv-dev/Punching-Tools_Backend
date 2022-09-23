package ru.mrdvernik.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MetalDeltaTable : Table(name = "metal__delta") {
    val metal = reference("metal", MetalsTable)
    val delta = reference("delta", DeltaTable, onDelete = ReferenceOption.CASCADE)
}
