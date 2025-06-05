package dev.zornov.repomine.audio

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos

data class VolumeSetting(
    val volume: Double,
    val minVoice: Double = volume,
    val position: Point = Pos(0.0, 0.0, 0.0)
)