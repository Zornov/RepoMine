package dev.zornov.repomine.config

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec

data class SaleZoneConfig(
    val start: Point,
    val end: Point,
    val labelPosition: Point,
    val labelRotation: Vec
)
