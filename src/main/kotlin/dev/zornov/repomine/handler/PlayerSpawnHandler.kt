package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.player.RepoPlayer
import dev.zornov.repomine.resourcepack.ResourcePackService
import dev.zornov.repomine.resourcepack.hud.HudService
import dev.zornov.repomine.scene.RepoScene
import dev.zornov.repomine.scene.SceneHud
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.timer.Scheduler
import java.time.Duration

@Singleton
class PlayerSpawnHandler(
    val rpServer: ResourcePackService,
    val hudService: HudService,
    val scheduler: Scheduler
) : EventListener {

    val scene = RepoScene()

    @EventHandler
    fun handle(event: PlayerSpawnEvent) {
        val player: RepoPlayer = event.player as RepoPlayer
        player.gameMode = GameMode.SURVIVAL
        player.sendResourcePacks(rpServer.request)

//        scene.addPlayer(player)
        val screen = SceneHud()
        hudService.run {
            player.showScreen(screen)
        }

        scheduler.buildTask {
            screen.helloWorldText.modifyFor(player) {
                text = Component.text("World!")
                horizontalPadding = 250
                verticalPadding = -50
            }
            hudService.updateScreen(player)
        }.delay(Duration.ofSeconds(10)).schedule()


    }
}
