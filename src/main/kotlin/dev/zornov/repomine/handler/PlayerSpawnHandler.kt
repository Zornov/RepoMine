package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.player.RepoPlayer
import dev.zornov.repomine.resourcepack.ResourcePackService
import dev.zornov.repomine.resourcepack.hud.HudService
import dev.zornov.repomine.resourcepack.hud.widget.TextWidget
import dev.zornov.repomine.scene.RepoScene
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

        scene.addPlayer(player)
        val welcomeText = TextWidget(
            id = "welcome",
            Component.text("Добро пожаловать!"),
            verticalPadding = -180,
            horizontalPadding = 0
        )

        hudService.run {
            player.addPlayerHudComponent(welcomeText)
        }

        scheduler.buildTask {
            hudService.run {
                player.getHudComponent<TextWidget>("welcome")?.text = Component.text("Hello")
            }
        }.delay(Duration.ofSeconds(20)).schedule()


    }
}
