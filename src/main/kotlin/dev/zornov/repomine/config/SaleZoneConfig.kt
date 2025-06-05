package dev.zornov.repomine.config

import net.minestom.server.coordinate.Point

data class SaleZoneConfig(
    val from: Point,
    val to: Point,
    val labelPosition: Point,
    val labelRotation: Triple<Double, Double, Double>
)