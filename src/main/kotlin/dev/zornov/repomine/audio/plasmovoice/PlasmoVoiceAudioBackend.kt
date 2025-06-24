package dev.zornov.repomine.audio.plasmovoice

import dev.zornov.repomine.audio.api.AudioBackend
import dev.zornov.repomine.audio.api.VolumeSetting
import dev.zornov.repomine.audio.api.WavDecoder
import net.minestom.server.entity.Player
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.line.ServerSourceLine
import su.plo.voice.api.server.audio.provider.ArrayAudioFrameProvider
import su.plo.voice.api.server.audio.source.ServerDirectSource
import su.plo.voice.api.server.player.VoicePlayer
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.sound.sampled.AudioSystem

class PlasmoVoiceAudioBackend(
    val voiceServer: PlasmoVoiceServer,
    val playerThreads: ConcurrentHashMap<Player, MutableList<Thread>>
) : AudioBackend {

    override fun playFile(player: Player, file: File, volumeConfig: VolumeSetting?) {
        try {
            val pcm = WavDecoder.decodeWavToPcm(file)

            AudioSystem.getAudioInputStream(file).use { ais ->
                val channels = ais.format.channels
                val adjusted = volumeConfig?.let { applyVolume(pcm, it) } ?: pcm
                val finalSamples = if (channels > 1) toMono(adjusted, channels) else adjusted
                sendToVoice(player, finalSamples, "PlasmoVoiceMonitor-${player.username}-${file.name}")
            }
        } catch (e: Exception) {
            player.sendMessage("Failed to play audio via PlasmoVoice: ${e.message}")
        }
    }

    override fun playSamples(player: Player, samples: ShortArray, volumeConfig: VolumeSetting?) {
        try {
            val adjusted = volumeConfig?.let { applyVolume(samples, it) } ?: samples
            sendToVoice(player, adjusted, "PlasmoVoiceMonitor-Samples-${player.username}")
        } catch (e: Exception) {
            player.sendMessage("Failed to play samples via PlasmoVoice: ${e.message}")
        }
    }

    override fun stop(player: Player) {
        playerThreads.remove(player)?.forEach { it.interrupt() }
    }

    fun sendToVoice(player: Player, samples: ShortArray, threadName: String) {
        val sourceLine: ServerSourceLine = voiceServer
            .sourceLineManager
            .getLineByName("proximity")
            .orElseThrow { IllegalStateException("Source line 'proximity' not found") }

        val voicePlayer: VoicePlayer = voiceServer
            .playerManager
            .getPlayerByName(player.username)
            .orElseThrow { IllegalStateException("VoicePlayer for '${player.username}' not found") }

        val directSource: ServerDirectSource = sourceLine.createDirectSource(voicePlayer, false)
        val provider = ArrayAudioFrameProvider(voiceServer, false).apply {
            addSamples(samples)
        }

        val sender = directSource.createAudioSender(provider).also { it.start() }
        sender.onStop {
            provider.close()
            directSource.remove()
        }

        val monitor = Thread {
            try { while (!Thread.currentThread().isInterrupted) Thread.sleep(500) }
            catch (_: InterruptedException) { /* Thread interrupted, exit */ }
        }.apply {
            isDaemon = true
            name = threadName
        }

        playerThreads.computeIfAbsent(player) { CopyOnWriteArrayList() }.add(monitor)
        monitor.start()
    }

    fun toMono(samples: ShortArray, channels: Int): ShortArray {
        if (channels <= 1) return samples
        val monoLen = samples.size / channels
        val mono = ShortArray(monoLen)
        for (i in 0 until monoLen) {
            var sum = 0
            val base = i * channels
            for (c in 0 until channels) sum += samples[base + c].toInt()
            mono[i] = (sum / channels).toShort()
        }
        return mono
    }

    fun applyVolume(samples: ShortArray, cfg: VolumeSetting): ShortArray {
        val factor = cfg.volume.coerceIn(0.0, 1.0)
        return ShortArray(samples.size) { idx ->
            val scaled = (samples[idx] * factor).toInt()
            when {
                scaled > Short.MAX_VALUE -> Short.MAX_VALUE
                scaled < Short.MIN_VALUE -> Short.MIN_VALUE
                else -> scaled.toShort()
            }
        }
    }
}
