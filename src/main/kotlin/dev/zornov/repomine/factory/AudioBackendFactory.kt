package dev.zornov.repomine.factory

import dev.zornov.repomine.audio.api.AudioBackend
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.plasmavoice.PlasmoVoiceAudioBackend
import dev.zornov.repomine.audio.vanilla.VanillaAudioBackend
import io.micronaut.context.annotation.Factory
import jakarta.inject.Provider
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
        voiceServerProvider: Provider<PlasmoVoiceServer>
    ): PlasmoVoiceAudioBackend {
        val server = voiceServerProvider.get()
        return PlasmoVoiceAudioBackend(
            voiceServer = server,
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
