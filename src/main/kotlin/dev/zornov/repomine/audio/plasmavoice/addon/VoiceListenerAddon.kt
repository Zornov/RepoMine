package dev.zornov.repomine.audio.plasmavoice.addon

import dev.zornov.repomine.audio.AudioType
import dev.zornov.repomine.audio.playerAudio
import dev.zornov.repomine.audio.vanilla.audio.AudioFrame
import dev.zornov.repomine.audio.vanilla.audio.AudioSource
import dev.zornov.repomine.audio.vanilla.audio.ShortArrayWavSource
import dev.zornov.repomine.audio.vanilla.sink.MinecraftNoteSink
import net.minestom.server.MinecraftServer
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.addon.InjectPlasmoVoice
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
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket


@Addon(
    id = "repomine-voice-addon",
    name = "RepoMine Voice Addon",
    version = "1.0.2",
    authors = ["Zorin"],
)
class VoiceListenerAddon : AddonInitializer {

    @InjectPlasmoVoice
    lateinit var voiceServer: PlasmoVoiceServer


    private var proximityHelper: ProximityServerActivationHelper? = null

    override fun onAddonInitialize() {
        voiceServer.eventBus.register(this, EventListener(voiceServer))

        val activation = voiceServer.activationManager
            .getActivationByName("proximity")
            .orElseThrow { IllegalStateException("Proximity activation not found") }

        val sourceLine = voiceServer.sourceLineManager
            .getLineByName("proximity")
            .orElseThrow { IllegalStateException("Proximity source line not found") }

        proximityHelper = ProximityServerActivationHelper(
            voiceServer,
            activation,
            sourceLine,
            object : ProximityServerActivationHelper.DistanceSupplier {
                override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioPacket): Short = 200
                override fun getDistance(player: VoiceServerPlayer, packet: PlayerAudioEndPacket): Short = 200
            }
        ).also { it.registerListeners(this) }

    }

    class EventListener(voiceServer: PlasmoVoiceServer) {
        val decoder: AudioDecoder = voiceServer.createOpusDecoder(false)
        val encryption: Encryption = voiceServer.defaultEncryption
        val sink = MinecraftNoteSink()

        @EventSubscribe
        fun onClientFullyConnected(event: UdpClientConnectedEvent) {
            val playerId = event.connection.player.createPlayerInfo().playerId
            playerAudio[playerId] = AudioType.VANILLA // TODO: SWAP
        }

        @EventSubscribe
        fun onPlayerSpeak(event: ServerSourceAudioPacketEvent) {
            event.activationInfo ?: return

            val pcmSamples: ShortArray = try {
                val decrypted = encryption.decrypt(event.packet.data)
                decoder.decode(decrypted)
            } catch (ex: CodecException) {
                return
            }

            val source: AudioSource = ShortArrayWavSource(pcmSamples)
            val audioFrame: AudioFrame = source.getCurrent()

            val recipients = MinecraftServer.getConnectionManager().onlinePlayers

            if (recipients.isEmpty()) {
                return
            }

            val vol = 50.0

            recipients.forEach { player ->
                val playerPos = player.position
                sink.playFrame(audioFrame, vol, player, playerPos)
            }
        }
    }
}
