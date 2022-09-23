package ru.mrdvernik.models


@kotlinx.serialization.Serializable
data class Metal(
    var id: Int? = null,
    var name: String,
    var deltas: List<Delta> = emptyList()
)

