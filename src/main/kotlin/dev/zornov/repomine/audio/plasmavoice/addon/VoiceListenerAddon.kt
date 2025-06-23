package dev.zornov.repomine.audio.plasmavoice.addon

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import dev.zornov.repomine.audio.api.AudioType
import dev.zornov.repomine.audio.api.playerAudio
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import dev.zornov.repomine.player.RepoPlayer
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.annotation.Addon
import su.plo.voice.api.audio.codec.AudioDecoder
import su.plo.voice.api.encryption.Encryption
import su.plo.voice.api.event.EventSubscribe
import su.plo.voice.api.server.audio.capture.ProximityServerActivationHelper
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEndEvent
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent
import su.plo.voice.api.server.event.connection.UdpClientConnectedEvent
import su.plo.voice.api.server.player.VoicePlayer
import su.plo.voice.api.server.player.VoiceServerPlayer
import su.plo.voice.minestom.MinestomVoiceServer
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket
import java.util.*
import java.util.concurrent.TimeUnit

@Singleton
@Addon(id = "voice-addon", name = "RepoMine Voice Addon", version = "1.0.0", authors = ["Zorin"])
class VoiceListenerAddon(
    val voiceServer: MinestomVoiceServer
) : AddonInitializer {

    override fun onAddonInitialize() {
        val activation = voiceServer.activationManager.getActivationByName("proximity")
            .orElseThrow { error("Proximity activation not found") }
        val sourceLine = voiceServer.sourceLineManager.getLineByName("proximity")
            .orElseThrow { error("Proximity source line not found") }

        voiceServer.eventBus.register(this, RepoVoiceListener())

        activation.onPlayerActivationStart {
            getRepoPlayer(it.instance.uuid)?.entity?.animationHandler?.playRepeat("mouth_speak")
        }

        ProximityServerActivationHelper(voiceServer, activation, sourceLine, object : ProximityServerActivationHelper.DistanceSupplier {
            val distance = 200.toShort()

            override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioPacket) = distance
            override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioEndPacket) = distance
        }).registerListeners(this)
    }

    inner class RepoVoiceListener {
        val encryption by lazy { voiceServer.defaultEncryption }
        val decoders: LoadingCache<VoicePlayer, AudioDecoder> =
            CacheBuilder
                .newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .removalListener<VoicePlayer, AudioDecoder> {
                    it.value?.close()
                }.build(
                    object : CacheLoader<VoicePlayer, AudioDecoder>() {
                        override fun load(key: VoicePlayer): AudioDecoder = voiceServer.createOpusDecoder(false)
                    },
                )
        val sink = MinecraftNoteSink()

        @EventSubscribe
        fun onClientConnected(event: UdpClientConnectedEvent) {
            playerAudio[event.connection.player.createPlayerInfo().playerId] = AudioType.PLASMO_VOICE
        }

        // TODO: Fix lol, break plasmovoice
//        @EventSubscribe(ignoreCancelled = false)
//        fun onPlayerSpeak(event: PlayerSpeakEvent) {
//            val pcmSamples = try {
//                decoder.decode(encryption.decrypt(event.packet.data))
//            } catch (_: CodecException) {
//                return
//            }
//
//            val audioFrame: AudioFrame = ShortArrayWavSource(pcmSamples).getCurrent()
//            MinecraftServer.getConnectionManager().onlinePlayers
//                .filter { it.getAudioType() != AudioType.PLASMO_VOICE }
//                .forEach { sink.playFrame(audioFrame, 50.0, it, it.position) }
//
//            // TODO: Put in async
//            // speechMemoryManager.addSamples(event.player.instance.uuid, pcmSamples)
//        }
        @EventSubscribe(ignoreCancelled = false)
        fun onPlayerSpeak(event: PlayerSpeakEvent) {
            val decrypted = encryption.decrypt(event.packet.data)
            val decoded = decoders.get(event.player).decode(decrypted)
            println(decoded.size)
        }

        @EventSubscribe(ignoreCancelled = false)
        fun onPlayerAudioEnd(event: PlayerSpeakEndEvent) {
            getRepoPlayer(event.player.instance.uuid)?.entity?.animationHandler?.run {
                stopRepeat("mouth_speak")
                playOnce("mouth_close") {}
            }
        }
    }

    fun getRepoPlayer(uuid: UUID): RepoPlayer? =
        MinecraftServer.getConnectionManager().onlinePlayers
            .find { it.uuid == uuid } as? RepoPlayer
}