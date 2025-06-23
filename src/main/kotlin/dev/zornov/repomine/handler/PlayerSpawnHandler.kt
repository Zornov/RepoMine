package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.resourcepack.ResourcePackService
import jakarta.inject.Singleton
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent

@Singleton
class PlayerSpawnHandler(
    val rpServer: ResourcePackService
) : EventListener {

    @EventHandler
    fun handle(event: PlayerSpawnEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.SURVIVAL
        player.sendResourcePacks(rpServer.request)

    }
}
