package dev.zornov.repomine.audio.vanilla.sink

import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player

interface AudioSink {
    fun playFrame(
        frame: AudioFrame,
        volume: Double,
        player: Player,
        position: Point
    )
}