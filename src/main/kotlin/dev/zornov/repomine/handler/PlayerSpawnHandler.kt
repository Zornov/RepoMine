package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.MinestomEvent
import jakarta.inject.Singleton
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent

@Singleton
class PlayerSpawnHandler : MinestomEvent<PlayerSpawnEvent>() {

    override fun handle(event: PlayerSpawnEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.CREATIVE



    }
}
