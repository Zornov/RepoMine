package dev.zornov.repomine.audio

import dev.zornov.repomine.audio.api.AudioBackend
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.api.VolumeSetting
import dev.zornov.repomine.audio.api.getAudioType
import dev.zornov.repomine.audio.exception.UnsupportedAudioTypeException
import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import jakarta.inject.Singleton
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDisconnectEvent
import java.io.File

@Singleton
class AudioPlayer(
    val backends: Map<AudioType, AudioBackend>,
    val speechMemoryManager: SpeechMemoryManager
) : EventListener {

    @EventHandler
    fun handle(event: PlayerDisconnectEvent) {
        stop(event.player)
        speechMemoryManager.clearPlayer(event.player.uuid)
    }

    fun play(player: Player, file: File, volumeSettings: VolumeSetting) {
        val backend = getAudioBackend(player)
        backend.playFile(player, file, volumeSettings)
    }

    fun play(player: Player, sampleArray: ShortArray, volumeSettings: VolumeSetting) {
        val backend = getAudioBackend(player)
        backend.playSamples(player, sampleArray, volumeSettings)
    }

    fun stop(player: Player) {
        backends.values.forEach { it.stop(player) }
    }

    fun getAudioBackend(player: Player): AudioBackend {
        val audioType = player.getAudioType()
        return backends[audioType] ?: throw UnsupportedAudioTypeException("No AudioBackend configured for type='$audioType'")
    }
}
