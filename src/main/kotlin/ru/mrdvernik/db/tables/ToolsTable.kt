package ru.mrdvernik.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import ru.mrdvernik.models.GeometricType
import ru.mrdvernik.models.ToolType

object ToolsTable : IntIdTable(name = "Tools_table") {
    val toolType = enumeration("tool_type", ToolType::class)
    val type = enumeration("type", GeometricType::class)
    val x = float("x")
    val y = float("y")
    val r = float("r").default(0f)
}
