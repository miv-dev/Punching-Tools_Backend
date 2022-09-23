package ru.mrdvernik.models

@kotlinx.serialization.Serializable
data class Tool(
    val id: Int = -1,
    val toolType: ToolType,
    val type: GeometricType,
    val x: Float,
    val y: Float = x,
    val r: Float = 0.0f
)

