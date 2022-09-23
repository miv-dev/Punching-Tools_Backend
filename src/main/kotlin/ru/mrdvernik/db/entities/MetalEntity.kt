package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.MetalDeltaTable
import ru.mrdvernik.db.tables.MetalsTable
import ru.mrdvernik.models.Metal

class MetalEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, MetalEntity>(MetalsTable)

    var name by MetalsTable.name
    var delta by DeltaEntity via MetalDeltaTable

    fun toDomain() = Metal(id.value, name, delta.map { it.toDomain() })
}
