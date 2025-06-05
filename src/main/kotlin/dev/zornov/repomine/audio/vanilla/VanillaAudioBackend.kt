package dev.zornov.repomine.audio.vanilla

import dev.zornov.repomine.audio.api.AudioBackend
import dev.zornov.repomine.audio.api.VolumeSetting
import dev.zornov.repomine.audio.vanilla.audio.ShortArrayWavSource
import dev.zornov.repomine.audio.vanilla.audio.WavStreamSource
import dev.zornov.repomine.audio.vanilla.context.MusicPlayerContext
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import net.minestom.server.entity.Player
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class VanillaAudioBackend(
    val playerThreads: ConcurrentHashMap<Player, MutableList<Thread>>
) : AudioBackend {
    val sink: MinecraftNoteSink = MinecraftNoteSink()

    override fun playFile(player: Player, file: File, volumeConfig: VolumeSetting?) {
        val settings = volumeConfig ?: throw IllegalArgumentException("VolumeSettings required for VanillaAudio")

        val source = WavStreamSource(file)
        val ctx = MusicPlayerContext(source, sink, settings, player)

        val thread = Thread(ctx).apply {
            isDaemon = true
            name = "VanillaAudio-${player.username}-${file.name}"
        }

        playerThreads.computeIfAbsent(player) { CopyOnWriteArrayList() }.add(thread)
        thread.start()
    }

    override fun playSamples(
        player: Player,
        samples: ShortArray,
        volumeConfig: VolumeSetting?
    ) {
        val settings = volumeConfig ?: throw IllegalArgumentException("VolumeSettings required for VanillaAudio")

        val source = ShortArrayWavSource(samples)

        val ctx = MusicPlayerContext(source, sink, settings, player)

        val thread = Thread(ctx).apply {
            isDaemon = true
            name = "VanillaAudio-${player.username}-Samples"
        }

        playerThreads.computeIfAbsent(player) { CopyOnWriteArrayList() }.add(thread)
        thread.start()
    }

    override fun stop(player: Player) {
        playerThreads.remove(player)?.forEach { it.interrupt() }
    }
}