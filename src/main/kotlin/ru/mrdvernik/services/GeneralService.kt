package ru.mrdvernik.services

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.mrdvernik.db.entities.MetalEntity
import ru.mrdvernik.db.entities.ToolEntity
import ru.mrdvernik.db.tables.ToolsTable
import ru.mrdvernik.models.Tool
import ru.mrdvernik.models.ToolType
import ru.mrdvernik.routes.GetAvailableTools

class GeneralService {
    suspend fun availableTools(filters: GetAvailableTools) = newSuspendedTransaction {
        val kits = mutableListOf<Kit>()
        MetalEntity[filters.metalId].delta.find { deltaEntity ->
            deltaEntity.thickness.id.value == filters.thicknessId
        }?.apply {
            val tools = ToolEntity.find { ToolsTable.type eq filters.geometricType }
            val punches = tools.filter { it.toolType == ToolType.Punch }
            punches.forEach { punch ->
                val delta = punch.x + value
                val dies = tools.filter {
                    it.x == delta && it.toolType == ToolType.Die
                }
                val strippers = tools.filter {
                    it.x in punch.x + 1..punch.x + 3 && it.toolType == ToolType.Stripper
                }
                if (dies.isNotEmpty() || strippers.isNotEmpty()) {
                    kits.add(Kit(punch.toDomain(), dies.map { it.toDomain() }, strippers.map { it.toDomain() }))
                }
            }
        }

        kits
    }
}

@Serializable
data class Kit(
    val punch: Tool,
    val dies: List<Tool> = emptyList(),
    val strippers: List<Tool> = emptyList()
)
