package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.TokensTable
import ru.mrdvernik.models.RefreshTokenFromDB

class TokenEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, TokenEntity>(TokensTable)

    var userId by TokensTable.userId
    var refreshToken by TokensTable.refreshToken
    var expiresAt by TokensTable.expiresAt

    fun toDomain() = RefreshTokenFromDB(userId, refreshToken, expiresAt)
}
