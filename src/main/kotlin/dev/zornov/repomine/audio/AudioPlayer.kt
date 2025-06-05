package dev.zornov.repomine.audio

import dev.zornov.repomine.audio.plasmavoice.MinestomPlasmaServer
import dev.zornov.repomine.audio.plasmavoice.PlasmoVoiceAudioBackend
import dev.zornov.repomine.audio.vanilla.VanillaAudioBackend
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import dev.zornov.repomine.common.api.MinestomEvent
import jakarta.inject.Singleton
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDisconnectEvent
import org.slf4j.Logger
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap

@Singleton
class AudioPlayer(
    minestomPlasmaServer: MinestomPlasmaServer,
    val logger: Logger
) : MinestomEvent<PlayerDisconnectEvent>() {
    val playerThreads = ConcurrentHashMap<Player, MutableList<Thread>>()

    val vanillaBackend = VanillaAudioBackend(
        sink = MinecraftNoteSink(),
        playerThreads = playerThreads
    )
    val plasmoBackend = PlasmoVoiceAudioBackend(
        minestomPlasmaServer = minestomPlasmaServer,
        playerThreads = playerThreads
    )

    val backends: Map<AudioType, AudioBackend> = mapOf(
        AudioType.VANILLA to vanillaBackend,
        AudioType.PLASMAVOICE to plasmoBackend
    )

    override fun handle(event: PlayerDisconnectEvent) {
        stop(event.player)
    }

    fun play(player: Player, file: File, volumeSettings: VolumeSetting) {
        val audioType = player.getAudioType()

        val backend = backends[audioType]
            ?: run {
                logger.error("No AudioBackend configured for type='{}'.", audioType)
                player.sendMessage("Audio type $audioType is not supported.")
                return
            }

        backend.playFile(player, file, volumeSettings)
    }

    fun play(player: Player, urlString: String, volumeSettings: VolumeSetting) {
        try {
            val uri = URI.create(urlString)
            val url = uri.toURL()
            val tempFile = Files.createTempFile("audio_stream_", ".wav").toFile().apply {
                deleteOnExit()
            }

            url.openStream().use { input ->
                Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            play(player, tempFile, volumeSettings)
        } catch (e: IllegalArgumentException) {
            player.sendMessage("Invalid URL format: ${e.message}")
        } catch (e: Exception) {
            logger.error("Failed to load audio from URL '{}': {}", urlString, e.message)
            player.sendMessage("Failed to load audio from URL: ${e.message}")
        }
    }

    fun stop(player: Player) {
        backends.values.forEach { it.stop(player) }
    }
}