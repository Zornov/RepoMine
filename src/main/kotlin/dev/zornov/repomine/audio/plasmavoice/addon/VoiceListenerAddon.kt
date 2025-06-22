package dev.zornov.repomine.audio.plasmavoice.addon

import dev.zornov.repomine.audio.SpeechMemoryManager
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.api.playerAudio
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import dev.zornov.repomine.audio.vanilla.audio.ShortArrayWavSource
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import dev.zornov.repomine.player.RepoPlayer
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.audio.codec.CodecException
import su.plo.voice.api.encryption.Encryption
import su.plo.voice.api.event.EventSubscribe
import su.plo.voice.api.server.audio.capture.ProximityServerActivationHelper
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEndEvent
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent
import su.plo.voice.api.server.event.audio.source.ServerSourceAudioPacketEvent
import su.plo.voice.api.server.event.connection.UdpClientConnectedEvent
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.minestom.MinestomVoiceServer
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket

@Singleton
@Addon(id = "voice-addon", name = "RepoMine Voice Addon", version = "1.0.0", authors = ["Zorin"])
class VoiceListenerAddon(
    val speechMemoryManager: SpeechMemoryManager,
    val voiceServer: MinestomVoiceServer
) : AddonInitializer {

    override fun onAddonInitialize() {
        voiceServer.eventBus.register(this, VoiceListener())

        val activation = voiceServer.activationManager.getActivationByName("proximity")
            .orElseThrow { error("Proximity activation not found") }
        val sourceLine = voiceServer.sourceLineManager.getLineByName("proximity")
            .orElseThrow { error("Proximity source line not found") }

        ProximityServerActivationHelper(voiceServer, activation, sourceLine, object : ProximityServerActivationHelper.DistanceSupplier {
            val distance = 200.toShort()
            override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioPacket) = distance
            override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioEndPacket) = distance
        }).registerListeners(this)
    }

    inner class VoiceListener {
        val decoder = voiceServer.createOpusDecoder(false)
        val encryption: Encryption = voiceServer.defaultEncryption
        val sink = MinecraftNoteSink()

        @EventSubscribe
        fun onClientConnected(event: UdpClientConnectedEvent) {
            playerAudio[event.connection.player.createPlayerInfo().playerId] = AudioType.PLASMO_VOICE
        }

        @EventSubscribe
        fun onPlayerSpeak(event: ServerSourceAudioPacketEvent) {
            val activationInfo = event.activationInfo ?: return
            event.result = ServerSourceAudioPacketEvent.Result.HANDLED

            val pcmSamples = try {
                decoder.decode(encryption.decrypt(event.packet.data))
            } catch (_: CodecException) {
                return
            }

            val audioFrame: AudioFrame = ShortArrayWavSource(pcmSamples).getCurrent()
            MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
                sink.playFrame(audioFrame, 50.0, player, player.position)
            }

            val playerId = activationInfo.player.instance.uuid
            speechMemoryManager.addSamples(playerId, pcmSamples)
        }

        @EventSubscribe(ignoreCancelled = false)
        fun onPlayerAudioStart(event: PlayerSpeakEvent) {
            println("test")
            getRepoPlayer(event.player.instance.uuid)?.entity?.animationHandler?.playRepeat("mouth_speak")
        }

        @EventSubscribe
        fun onPlayerAudioEnd(event: PlayerSpeakEndEvent) {
            getRepoPlayer(event.player.instance.uuid)?.entity?.animationHandler?.stopRepeat("mouth_speak")
        }

        fun getRepoPlayer(uuid: java.util.UUID): RepoPlayer? =
            MinecraftServer.getConnectionManager().onlinePlayers
                .find { it.uuid == uuid } as? RepoPlayer
    }
}
