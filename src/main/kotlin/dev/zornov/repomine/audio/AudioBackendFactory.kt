package dev.zornov.repomine.audio

import dev.zornov.repomine.audio.api.AudioBackend
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.plasmovoice.PlasmoVoiceAudioBackend
import dev.zornov.repomine.audio.vanilla.VanillaAudioBackend
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import net.minestom.server.entity.Player
import su.plo.voice.api.server.PlasmoVoiceServer
import java.util.concurrent.ConcurrentHashMap

@Factory
class AudioBackendFactory {

    @Singleton
    fun playerThreads(): ConcurrentHashMap<Player, MutableList<Thread>> {
        return ConcurrentHashMap()
    }

    @Singleton
    fun vanillaBackend(
        threads: ConcurrentHashMap<Player, MutableList<Thread>>
    ): VanillaAudioBackend {
        return VanillaAudioBackend(
            playerThreads = threads
        )
    }


    @Singleton
    fun plasmoBackend(
        threads: ConcurrentHashMap<Player, MutableList<Thread>>,
        voiceServer: PlasmoVoiceServer
    ): PlasmoVoiceAudioBackend {
        return PlasmoVoiceAudioBackend(
            voiceServer = voiceServer,
            playerThreads = threads
        )
    }

    @Singleton
    fun backendsMap(
        vanilla: VanillaAudioBackend,
        plasmo: PlasmoVoiceAudioBackend
    ): Map<AudioType, AudioBackend> {
        return mapOf(
            AudioType.VANILLA to vanilla,
            AudioType.PLASMO_VOICE to plasmo
        )
    }
}