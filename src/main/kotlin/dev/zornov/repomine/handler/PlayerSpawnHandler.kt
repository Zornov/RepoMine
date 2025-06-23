package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import dev.zornov.repomine.player.RepoPlayer
import dev.zornov.repomine.resourcepack.ResourcePackService
import dev.zornov.repomine.scene.RepoScene
import jakarta.inject.Singleton
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerSpawnEvent

@Singleton
class PlayerSpawnHandler(
    val rpServer: ResourcePackService
) : EventListener {

    val scene = RepoScene()

    @EventHandler
    fun handle(event: PlayerSpawnEvent) {
        val player: RepoPlayer = event.player as RepoPlayer
        player.gameMode = GameMode.SURVIVAL
        player.sendResourcePacks(rpServer.request)

        scene.addPlayer(player)

        ApexPredatorEntity(
            player.instance, Pos(1.0, 40.0, 2.0)
        ).apply { scene.addMonster(this) }


    }
}
