package ru.mrdvernik.services

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mrdvernik.db.entities.ToolEntity
import ru.mrdvernik.db.tables.ToolsTable
import ru.mrdvernik.models.GeometricType
import ru.mrdvernik.models.Tool

class ToolsService {
    init {
        transaction {
            SchemaUtils.create(ToolsTable)
        }
    }

    suspend fun all() = newSuspendedTransaction {
        ToolEntity.all().map { it.toDomain() }
    }

    suspend fun create(tool: Tool) = newSuspendedTransaction {
        ToolEntity.new {
            toolType = tool.toolType
            type = tool.type
            x = tool.x
            when (tool.type) {
                GeometricType.RND, GeometricType.SQR ->
                    y = tool.x

                GeometricType.RECTA, GeometricType.OVAL ->
                    y = tool.y

                GeometricType.RECTARAD, GeometricType.SPECIAL -> {
                    x = tool.x
                    y = tool.y
                    r = tool.r
                }
            }

        }
    }

    suspend fun update(tool: Tool) = newSuspendedTransaction {
        ToolEntity[tool.id].apply {
            toolType = tool.toolType
            type = tool.type
            x = tool.x
            y = tool.y
            r = tool.r
        }
    }

    suspend fun delete(id: Int) = newSuspendedTransaction {
        ToolEntity[id].delete()
    }

    suspend fun deleteCollection(ids: List<Int>)= newSuspendedTransaction {
        ids.forEach {
            ToolEntity[it].delete()
        }
    }
}
