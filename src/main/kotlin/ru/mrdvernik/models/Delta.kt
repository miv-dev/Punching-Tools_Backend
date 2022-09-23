package ru.mrdvernik.models


@kotlinx.serialization.Serializable
data class Delta(
    var id: Int? = null,
    var value: Float,
    var thickness: MetalThickness
)
