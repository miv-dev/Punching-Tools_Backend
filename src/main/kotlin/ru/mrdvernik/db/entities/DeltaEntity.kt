package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.DeltaTable
import ru.mrdvernik.models.Delta

class DeltaEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, DeltaEntity>(DeltaTable)

    var value by DeltaTable.value
    var thickness by MetalThicknessEntity referencedOn DeltaTable.thickness

    fun toDomain() = Delta(id.value, value, thickness.toDomain())
}

