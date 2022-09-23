package ru.mrdvernik.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import ru.mrdvernik.utils.serializers.UUIDSerializer
import java.util.*

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val username: String = "",
    val password: String,
    val email: String,
    val isAdmin: Boolean = false,
) : Principal
