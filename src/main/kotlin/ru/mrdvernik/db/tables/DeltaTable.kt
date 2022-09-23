package ru.mrdvernik.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DeltaTable : IntIdTable(name = "delta_table") {
    val value = float("delta")
    val thickness = reference("thickness", MetalThicknessTable, onDelete = ReferenceOption.CASCADE)
}
