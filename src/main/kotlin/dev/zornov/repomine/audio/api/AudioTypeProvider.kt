package dev.zornov.repomine.audio.api

import net.minestom.server.entity.Player
import java.util.*

enum class AudioType {
    VANILLA,
    PLASMO_VOICE
}

val playerAudio = hashMapOf<UUID, AudioType>()

fun Player.getAudioType(): AudioType {
    playerAudio[uuid]?.let { return it }
    return AudioType.VANILLA
}