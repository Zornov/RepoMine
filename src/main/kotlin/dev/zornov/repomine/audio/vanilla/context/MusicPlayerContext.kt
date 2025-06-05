package dev.zornov.repomine.audio.vanilla.context

import dev.zornov.repomine.audio.api.VolumeSetting
import dev.zornov.repomine.audio.vanilla.api.AudioSource
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import dev.zornov.repomine.audio.vanilla.sink.AudioSink
import net.minestom.server.entity.Player

class MusicPlayerContext(
    val source: AudioSource,
    val sink: AudioSink,
    val volumeSettings: VolumeSetting,
    val player: Player
) : Runnable {

    override fun run() {
        val startMs = System.currentTimeMillis()
        while (player.isOnline && source.prepareNext()) {
            val frame: AudioFrame = source.getCurrent()

            val delay = (startMs + frame.frameStartMs - System.currentTimeMillis()).coerceAtLeast(0.0)
            if (delay > 0) {
                try { Thread.sleep(delay.toLong()) } catch (_: InterruptedException) { break }
            }

            var vol = volumeSettings.volume
            var pos = volumeSettings.position
            val playerPos = player.position
            val dist = playerPos.distance(pos)
            if (vol - dist / 16.0 < volumeSettings.minVoice) {
                vol = volumeSettings.minVoice
                pos = playerPos
            }
            sink.playFrame(frame, vol, player, pos)
        }
    }
}