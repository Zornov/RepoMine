package dev.zornov.repomine.resourcepack.hud

import dev.zornov.repomine.resourcepack.hud.builder.DisplayType
import dev.zornov.repomine.resourcepack.hud.builder.HudScreen
import dev.zornov.repomine.resourcepack.hud.builder.annotations.Position
import dev.zornov.repomine.resourcepack.hud.builder.annotations.Widget
import dev.zornov.repomine.resourcepack.hud.widget.TextWidget
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import jakarta.inject.Singleton
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.network.ConnectionManager
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule
import java.util.*

@Singleton
class HudService(
    val scheduler: Scheduler,
    val connectionManager: ConnectionManager
) : ApplicationEventListener<StartupEvent> {

    private val playerScreens = mutableMapOf<UUID, HudScreen>()
    private val playerTextMap = mutableMapOf<UUID, Component>()
    private val playerBossBar = mutableMapOf<UUID, BossBar>()

    override fun onApplicationEvent(event: StartupEvent) {
        scheduler.buildTask {
            connectionManager.onlinePlayers.forEach { player ->
                playerTextMap[player.uuid]?.let(player::sendActionBar)
            }
        }.repeat(TaskSchedule.tick(20)).schedule()
    }

    fun Player.showScreen(screen: HudScreen) {
        playerScreens[uuid] = screen
        updateScreen(this)
    }

    fun updateScreen(player: Player) {
        val screen = playerScreens[player.uuid] ?: return

        val widgets = screen.javaClass.declaredFields
            .asSequence()
            .filter {
                it.isAnnotationPresent(Widget::class.java) && it.isAnnotationPresent(Position::class.java)
            }
            .mapNotNull { field ->
                runCatching {
                    field.isAccessible = true
                    val widget = field.get(screen) as? TextWidget ?: return@mapNotNull null
                    if (!widget.isVisible) return@mapNotNull null
                    field.getAnnotation(Position::class.java).also {
                        widget.horizontalPadding = it.x
                        widget.verticalPadding = it.y
                    }
                    widget
                }.getOrElse {
                    it.printStackTrace()
                    null
                }
            }
            .sortedWith(compareBy({ it.verticalPadding }, { it.horizontalPadding }))

        val textComponent = widgets
            .fold(Component.text()) { acc, widget -> acc.append(widget.getComponent(player.uuid)) }
            .build()

        when (screen.displayType) {
            DisplayType.ACTION_BAR -> {
                playerTextMap[player.uuid] = textComponent
            }
            DisplayType.BOSS_BAR -> {
                if (playerBossBar.containsKey(player.uuid)) {
                    val bar = playerBossBar[player.uuid] ?: return
                    bar.name(textComponent)
                } else {
                    val bossBar = BossBar.bossBar(
                        textComponent,
                        1f,
                        BossBar.Color.PURPLE,
                        BossBar.Overlay.PROGRESS
                    )
                    player.showBossBar(bossBar)
                }
            }
        }
    }
}
