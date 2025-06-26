package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import dev.zornov.repomine.ext.toKey
import dev.zornov.repomine.player.RepoPlayer
import dev.zornov.repomine.resourcepack.ResourcePackService
import dev.zornov.repomine.resourcepack.hud.HudService
import dev.zornov.repomine.resourcepack.hud.widget.TextWidget
import dev.zornov.repomine.scene.RepoScene
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerSpawnEvent

@Singleton
class PlayerSpawnHandler(
    val rpServer: ResourcePackService,
    val hudService: HudService
) : EventListener {

    val scene = RepoScene()

    @EventHandler
    fun handle(event: PlayerSpawnEvent) {
        val player: RepoPlayer = event.player as RepoPlayer
        player.gameMode = GameMode.SURVIVAL
        player.sendResourcePacks(rpServer.request)

        scene.addPlayer(player)
//
//        ApexPredatorEntity(
//            player.instance, Pos(1.0, 40.0, 2.0)
//        ).apply { scene.addMonster(this) }

        hudService.addGlobalHudComponent(TextWidget(
            "test",
            Component.text("100").font("green".toKey()),
            40,
            -200
        ))
        player.showBossBar(hudService.bossBar)


    }
}
