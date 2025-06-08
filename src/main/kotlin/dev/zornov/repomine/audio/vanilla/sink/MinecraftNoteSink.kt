package dev.zornov.repomine.audio.vanilla.sink

import dev.zornov.repomine.audio.api.SinWaves
import dev.zornov.repomine.audio.vanilla.SinewaveRegistry
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import jakarta.inject.Singleton
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.kyori.adventure.sound.Sound as AdventureSound

@Singleton
class MinecraftNoteSink {
    fun playFrame(
        frame: AudioFrame,
        volume: Double,
        player: Player,
        position: Point
    ) {
        val maxSounds = SinWaves.waves.size
        val sortedBins = frame.bins.asSequence()
            .sortedBy { -it.amplitude }
            .take(frame.bins.size / 4)
//            .filter { it.amplitude > 10 }
            .shuffled()
            .take(maxSounds * 2)
            .sortedBy { -it.amplitude }
            .take(maxSounds)
//            .take(maxSounds.coerceAtMost(8))
            .toList()

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

            player.playSound(sound, position.x(), position.y(), position.z())
        }

    }

}
