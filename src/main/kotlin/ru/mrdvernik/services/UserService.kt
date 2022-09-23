package ru.mrdvernik.services

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mrdvernik.db.entities.UserEntity
import ru.mrdvernik.db.tables.UserTable
import ru.mrdvernik.models.User
import java.util.*

class UserService {
    init {
        transaction {
//            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)
//            val newUser = UserEntity.new {
//                email = "i@mike39.ru"
//                username = "mike"
//                password ="hummer_16"
//                isAdmin = true
//            }.toDomain()
        }
    }

    suspend fun userByEmail(email: String): User? = newSuspendedTransaction {
        UserEntity.find { UserTable.email eq email }.firstOrNull()?.toDomain()
    }

    suspend fun userByUsername(username: String): User? = newSuspendedTransaction {
        if (username == "") null
        else UserEntity.find { UserTable.username eq username }.firstOrNull()?.toDomain()
    }

    suspend fun userById(userId: String): User = newSuspendedTransaction {
        UserEntity[UUID.fromString(userId)].toDomain()
    }

    suspend fun users(currentUserId: UUID): List<User> = newSuspendedTransaction {
        UserEntity.find { UserTable.id neq currentUserId }.map { it.toDomain() }
    }


    suspend fun create(user: User): Result<UUID> = newSuspendedTransaction {
        try {
            val newUser = UserEntity.new {
                email = user.email
                username = user.username
                password = user.password
                isAdmin = user.isAdmin
            }.toDomain()

            Result.success(newUser.id!!)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun delete(uuids: List<UUID>) = newSuspendedTransaction {
        uuids.forEach { uuid ->
            UserEntity[uuid].delete()

        }
    }
    suspend fun update(user: User) = newSuspendedTransaction {
        if (user.id != null){
            UserEntity[user.id].apply {
                email = user.email
                password = user.password
                username = user.username
            }
        }
    }
}
