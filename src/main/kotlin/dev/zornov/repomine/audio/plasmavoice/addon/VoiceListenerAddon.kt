package dev.zornov.repomine.audio.plasmavoice.addon

import dev.zornov.repomine.audio.SpeechMemoryManager
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.api.playerAudio
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import dev.zornov.repomine.audio.vanilla.audio.ShortArrayWavSource
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.audio.codec.AudioDecoder
import su.plo.voice.api.audio.codec.CodecException
import su.plo.voice.api.encryption.Encryption
import su.plo.voice.api.event.EventSubscribe
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.api.server.audio.capture.ProximityServerActivationHelper
import su.plo.voice.api.server.event.audio.source.ServerSourceAudioPacketEvent
import su.plo.voice.api.server.event.connection.UdpClientConnectedEvent
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.minestom.MinestomVoiceServer
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket

@Singleton
@Addon(
    id = "repomine-voice-addon",
    name = "RepoMine Voice Addon",
    version = "1.0.2",
    authors = ["Zorin"],
)
class VoiceListenerAddon(
    val speechMemoryManager: SpeechMemoryManager,
    val voiceServer: MinestomVoiceServer
) : AddonInitializer {

    override fun onAddonInitialize() {
        voiceServer.eventBus.register(this, VoiceListener(voiceServer, speechMemoryManager))

        val activation = voiceServer.activationManager
            .getActivationByName("proximity")
            .orElseThrow { IllegalStateException("Proximity activation not found") }

        val sourceLine = voiceServer.sourceLineManager
            .getLineByName("proximity")
            .orElseThrow { IllegalStateException("Proximity source line not found") }

        ProximityServerActivationHelper(
            voiceServer,
            activation,
            sourceLine,
            object : ProximityServerActivationHelper.DistanceSupplier {
                override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioPacket): Short = 200
                override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioEndPacket): Short = 200
            }
        ).also { it.registerListeners(this) }
    }

    @Suppress("unused")
    class VoiceListener(
        voiceServer: PlasmoVoiceServer,
        val speechMemoryManager: SpeechMemoryManager
    ) {
        val decoder: AudioDecoder = voiceServer.createOpusDecoder(false)
        val encryption: Encryption = voiceServer.defaultEncryption
        val sink = MinecraftNoteSink()

        @EventSubscribe
        fun onClientConnected(event: UdpClientConnectedEvent) {
            val playerId = event.connection.player.createPlayerInfo().playerId
            playerAudio[playerId] = AudioType.PLASMO_VOICE
        }

        @EventSubscribe
        fun onPlayerSpeak(event: ServerSourceAudioPacketEvent) {
            if (event.activationInfo == null) return

            val pcmSamples = try {
                decoder.decode(encryption.decrypt(event.packet.data))
            } catch (_: CodecException) {
                return
            }

            val audioFrame: AudioFrame = ShortArrayWavSource(pcmSamples).getCurrent()
            val recipients = MinecraftServer.getConnectionManager().onlinePlayers
            if (recipients.isNotEmpty()) {
                recipients.forEach { player ->
                    sink.playFrame(audioFrame, 50.0, player, player.position)
                }
            }

            event.activationInfo?.player?.createPlayerInfo()?.playerId
                ?.let { speechMemoryManager.addSamples(it, pcmSamples) }
        }
    }
}
