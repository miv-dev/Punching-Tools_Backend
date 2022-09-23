package ru.mrdvernik.services

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mrdvernik.db.entities.DeltaEntity
import ru.mrdvernik.db.entities.MetalEntity
import ru.mrdvernik.db.entities.MetalThicknessEntity
import ru.mrdvernik.db.tables.DeltaTable
import ru.mrdvernik.db.tables.MetalDeltaTable
import ru.mrdvernik.db.tables.MetalsTable
import ru.mrdvernik.models.Delta
import ru.mrdvernik.models.Metal
import ru.mrdvernik.models.MetalName
import ru.mrdvernik.routes.DeleteMetalDelta

class MetalService {
    init {
        transaction {
//            SchemaUtils.drop(MetalsTable, DeltaTable, MetalDeltaTable)
            SchemaUtils.createMissingTablesAndColumns(MetalsTable, DeltaTable, MetalDeltaTable)
        }

    }

    suspend fun all() = newSuspendedTransaction {
        MetalEntity.all().map { it.toDomain() }
    }
    suspend fun allNames() = newSuspendedTransaction {
        MetalEntity.all().map { MetalName(it.id.value, it.name)}
    }

    suspend fun addMetal(metal: Metal) = newSuspendedTransaction {
        MetalEntity.new {
            name = metal.name
            delta = SizedCollection(listOf())
        }
    }

    suspend fun deleteDelta(metalId: Int, deleteMetalDelta: DeleteMetalDelta): Result<Unit> = newSuspendedTransaction {
        println(DeltaEntity.all().map { it.toDomain() })
        deleteMetalDelta.idList.forEach {
            DeltaEntity.findById(it.toInt())?.delete()
        }
        println(DeltaEntity.all().map { it.toDomain() })
        Result.success(Unit)
    }

    suspend fun updateMetal(newMetal: Metal) = newSuspendedTransaction {
        val metal = MetalEntity.findById(newMetal.id ?: -1)
        if (metal !== null) {
            metal.name = newMetal.name
            val deltas: MutableList<DeltaEntity> = mutableListOf()
            newMetal.deltas.forEach { delta ->
                if (delta.id == -1) {
                    DeltaEntity.new {
                        value = delta.value
                        thickness = MetalThicknessEntity[delta.thickness.id ?: -1]
                    }.also {
                        deltas.add(it)
                    }
                } else {
                    DeltaEntity.findById(delta.id ?: -1)?.let {
                        it.value = delta.value
                        it.thickness = MetalThicknessEntity[delta.thickness.id ?: -1]
                        deltas.add(it)
                    }
                }

            }

            val toDelete = metal.delta.filter{ delta ->
                var u = false

                deltas.forEach {
                    if (delta.id == it.id) u = true
                }

                return@filter !u
            }

            toDelete.forEach {
                it.delete()
            }
            metal.delta = SizedCollection(deltas)

        }
    }

    suspend fun addDelta(metalId: Int, delta: Delta): Result<Unit> = newSuspendedTransaction {
        val metal = MetalEntity.findById(metalId)
        val thicknessEntity = MetalThicknessEntity.findById(delta.thickness.id ?: -1)

        if (metal != null) {
            if (thicknessEntity != null) {
                try {
                    var u = false
                    metal.delta.forEach {
                        if (it.thickness.id.value == delta.thickness.id) u = true
                    }
                    if (!u) {
                        val deltaEntity = DeltaEntity.new {
                            value = delta.value
                            thickness = thicknessEntity
                        }


                        metal.delta = SizedCollection(metal.delta + deltaEntity)
                    } else {
                        Result.failure<Exception>(Exception("This delta already exists!"))
                    }
                } catch (e: Exception) {
                    Result.failure<Exception>(e)
                }
            } else Result.failure<Exception>(Exception("This thickness doesn't exist!"))

        } else Result.failure<Exception>(Exception("This metal doesn't exist!"))


        Result.success(Unit)
    }
}
