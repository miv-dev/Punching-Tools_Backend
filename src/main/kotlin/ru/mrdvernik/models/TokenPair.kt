package ru.mrdvernik.models

@kotlinx.serialization.Serializable
data class TokenPair(val accessToken: String, val refreshToken: String)
