package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.ToolsTable
import ru.mrdvernik.models.Tool

class ToolEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, ToolEntity>(ToolsTable)

    var toolType by ToolsTable.toolType
    var type by ToolsTable.type
    var x by ToolsTable.x
    var y by ToolsTable.y
    var r by ToolsTable.r


    fun toDomain() = Tool(id.value, toolType, type, x, y, r)
}
