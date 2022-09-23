package ru.mrdvernik.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object MetalThicknessTable: IntIdTable("metal_thickness") {
    val value = float("thickness")
}
