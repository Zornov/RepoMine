package dev.zornov.repomine.audio

import net.minestom.server.entity.Player
import java.io.File

interface AudioBackend {

    fun playFile(
        player: Player,
        file: File,
        volumeConfig: VolumeSetting?
    )

    fun stop(player: Player)
}