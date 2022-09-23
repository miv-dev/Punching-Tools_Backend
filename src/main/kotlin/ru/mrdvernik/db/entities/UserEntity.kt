package ru.mrdvernik.db.entities

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import ru.mrdvernik.db.tables.UserTable
import ru.mrdvernik.models.User
import java.util.*

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : EntityClass<UUID, UserEntity>(UserTable)

    val uuid by UserTable.id
    var email by UserTable.email
    var username by UserTable.username
    var password by UserTable.password
    var isAdmin by UserTable.isAdmin

    fun toDomain() = User(uuid.value, username, password, email, isAdmin)
}
