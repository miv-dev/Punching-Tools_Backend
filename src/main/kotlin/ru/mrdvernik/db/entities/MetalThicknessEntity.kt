package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.MetalThicknessTable
import ru.mrdvernik.models.MetalThickness

class MetalThicknessEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, MetalThicknessEntity>(MetalThicknessTable)

    var value by MetalThicknessTable.value

    fun toDomain() = MetalThickness(id.value, value)
}
