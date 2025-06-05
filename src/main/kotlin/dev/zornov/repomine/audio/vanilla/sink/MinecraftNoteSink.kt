package dev.zornov.repomine.audio.vanilla.sink

import dev.zornov.repomine.audio.vanilla.SinWaves
import dev.zornov.repomine.audio.vanilla.SinewaveRegistry
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.kyori.adventure.sound.Sound as AdventureSound

class MinecraftNoteSink : AudioSink {
    override fun playFrame(
        frame: AudioFrame,
        volume: Double,
        player: Player,
        position: Point
    ) {
        val maxSounds = SinWaves.waves.size
        val sortedBins = frame.bins
            .sortedByDescending { it.amplitude }
            .take(maxSounds)

        for (bin in sortedBins) {
            val event = SinewaveRegistry.getBestSound(bin.frequency)
            val baseFreq = SinewaveRegistry.getFrequency(event)

            val pitch = (bin.frequency / baseFreq).toFloat().coerceIn(0.5f, 2.0f)
            val volumeRatio = bin.amplitude / 32768.0
            val mcVolume = (volume * volumeRatio * 2f).coerceIn(0.0, 1.0).toFloat()

            val sound = AdventureSound.sound(
                event.key(),
                AdventureSound.Source.VOICE,
                mcVolume,
                pitch
            )

            println("amp=${bin.amplitude}, volumeRatio=${bin.amplitude / 32768.0}, finalVol=$mcVolume, pitch=$pitch")

            player.playSound(sound, position.x(), position.y(), position.z())
        }

    }

}
