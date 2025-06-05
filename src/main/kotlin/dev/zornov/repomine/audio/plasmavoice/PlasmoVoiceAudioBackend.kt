package dev.zornov.repomine.audio.plasmavoice

import dev.zornov.repomine.audio.AudioBackend
import dev.zornov.repomine.audio.VolumeSetting
import dev.zornov.repomine.ext.WavDecoder
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
    val minestomPlasmaServer: MinestomPlasmaServer,
    val playerThreads: ConcurrentHashMap<Player, MutableList<Thread>>
) : AudioBackend {

    val voiceServer: PlasmoVoiceServer by lazy { minestomPlasmaServer.server }

    override fun playFile(
        player: Player,
        file: File,
        volumeConfig: VolumeSetting?
    ) {
        try {
            val pcmSamples: ShortArray = WavDecoder.decodeWavToPcm(file)
            val aisOriginal = AudioSystem.getAudioInputStream(file)
            val sourceFormat = aisOriginal.format
            val sourceChannels = sourceFormat.channels
            aisOriginal.close()

            val volumeAdjusted: ShortArray = volumeConfig?.let {
                applyVolume(pcmSamples, it)
            } ?: pcmSamples

            val monoSamples = if (sourceChannels > 1) {
                toMono(volumeAdjusted, sourceChannels)
            } else {
                volumeAdjusted
            }

            val sourceLine: ServerSourceLine = voiceServer
                .sourceLineManager
                .getLineByName("proximity")
                .orElseThrow { IllegalStateException("Source line 'proximity' not found") }

            val voicePlayer: VoicePlayer = voiceServer
                .playerManager
                .getPlayerByName(player.username)
                .orElseThrow { IllegalStateException("VoicePlayer for '${player.username}' not found") }

            val directSource: ServerDirectSource = sourceLine.createDirectSource(voicePlayer, false)
            val frameProvider = ArrayAudioFrameProvider(voiceServer, false)
            frameProvider.addSamples(monoSamples)

            val audioSender = directSource.createAudioSender(frameProvider)
            audioSender.start()

            audioSender.onStop {
                frameProvider.close()
                directSource.remove()
            }

            val monitorThread = Thread {
                try {
                    while (!Thread.currentThread().isInterrupted) {
                        Thread.sleep(500)
                    }
                } catch (_: InterruptedException) { }
            }.apply {
                isDaemon = true
                name = "PlasmoVoiceMonitor-${player.username}-${file.name}"
            }
            playerThreads.computeIfAbsent(player) { CopyOnWriteArrayList() }.add(monitorThread)
            monitorThread.start()

        } catch (e: Exception) {
            player.sendMessage("Failed to play audio via PlasmoVoice: ${e.message}")
        }
    }

    fun toMono(samples: ShortArray, channels: Int): ShortArray {
        if (channels == 1) return samples
        val monoLength = samples.size / channels
        val mono = ShortArray(monoLength)
        for (i in 0 until monoLength) {
            var sum = 0
            for (c in 0 until channels) {
                sum += samples[i * channels + c].toInt()
            }
            mono[i] = (sum / channels).toShort()
        }
        return mono
    }

    override fun stop(player: Player) {
        playerThreads.remove(player)?.forEach { it.interrupt() }
    }

    fun applyVolume(
        samples: ShortArray,
        volumeConfig: VolumeSetting
    ): ShortArray {
        val factor = volumeConfig.volume.coerceIn(0.0, 1.0)
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
