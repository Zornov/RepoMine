package dev.zornov.repomine.audio.api

import net.minestom.server.entity.Player
import java.io.File

interface AudioBackend {

    fun playFile(
        player: Player,
        file: File,
        volumeConfig: VolumeSetting?
    )

    fun playSamples(
        player: Player,
        samples: ShortArray,
        volumeConfig: VolumeSetting?
    )

    fun stop(player: Player)
}