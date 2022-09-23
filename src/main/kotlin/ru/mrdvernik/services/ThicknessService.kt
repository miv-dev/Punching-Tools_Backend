package ru.mrdvernik.services

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mrdvernik.db.entities.MetalThicknessEntity
import ru.mrdvernik.db.tables.MetalThicknessTable
import ru.mrdvernik.models.MetalThickness

class ThicknessService {
    init {
        transaction {
            SchemaUtils.create(MetalThicknessTable)
        }
    }

    suspend fun all(): List<MetalThickness> = newSuspendedTransaction {
        MetalThicknessEntity.all().map { it.toDomain() }
    }

    suspend fun add(metalThickness: MetalThickness): MetalThickness = newSuspendedTransaction {
        MetalThicknessEntity.new {
            value = metalThickness.value
        }.toDomain()
    }

    suspend fun delete(id: Int) = newSuspendedTransaction {
        MetalThicknessEntity[id].delete()
    }

    suspend fun deleteCollection(ids: List<Int>) = newSuspendedTransaction {
        ids.forEach {
            MetalThicknessEntity[it].delete()
        }
    }

    suspend fun migrate() = newSuspendedTransaction {
        SchemaUtils.checkMappingConsistence(MetalThicknessTable)
    }

    suspend fun update(metalThickness: MetalThickness) = newSuspendedTransaction {
        if (metalThickness.id != null)
            MetalThicknessEntity
                .findById(metalThickness.id)?.let {
                    it.value = metalThickness.value
                }

    }
}
