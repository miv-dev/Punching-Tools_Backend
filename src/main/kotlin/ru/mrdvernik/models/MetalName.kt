package ru.mrdvernik.models

import kotlinx.serialization.Serializable

@Serializable
data class MetalName(
    val id: Int,
    val value: String
)
