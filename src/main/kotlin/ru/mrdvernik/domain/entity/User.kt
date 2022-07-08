package ru.mrdvernik.domain.entity

@kotlinx.serialization.Serializable
data class User(
    val password: String,
    val email: String
)
