package dev.zornov.repomine.scene

import dev.zornov.repomine.config.SaleZoneConfig
import dev.zornov.repomine.player.RepoPlayer
import dev.zornov.repomine.repo.SaleZone
import net.minestom.server.instance.Instance

class RepoScene(
    val instance: Instance,
    saleZoneConfig: SaleZoneConfig
) {
    val players: MutableList<RepoPlayer> = mutableListOf()

    val saleZone = SaleZone(instance, saleZoneConfig)

    fun create() {
        saleZone.spawn()
    }

    fun update() {
        saleZone.update()
    }
}