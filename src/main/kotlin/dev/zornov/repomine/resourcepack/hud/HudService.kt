package dev.zornov.repomine.resourcepack.hud

import dev.zornov.repomine.ext.bottomPadding
import dev.zornov.repomine.ext.horizontalPadding
import dev.zornov.repomine.ext.topPadding
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
import kotlin.math.abs

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

        val paddedWidgets = screen::class.java.declaredFields
            .asSequence()
            .filter { it.isAnnotationPresent(Widget::class.java) }
            .mapNotNull { field ->
                try {
                    field.isAccessible = true
                    val widget = field.get(screen) as? TextWidget ?: return@mapNotNull null
                    if (!widget.isVisible) return@mapNotNull null

                    val position = field.getAnnotation(Position::class.java)
                    val vertical = position?.vertical ?: 0
                    val horizontal = position?.horizontal ?: 0

                    var padded = widget.getComponent(player.uuid)

                    padded = when {
                        vertical > 0 -> padded.topPadding(vertical)
                        vertical < 0 -> padded.bottomPadding(abs(vertical))
                        else -> padded
                    }

                    padded.horizontalPadding(horizontal)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            .toList()

        val textComponent = paddedWidgets
            .fold(Component.text()) { acc, comp -> acc.append(comp) }
            .build()

        when (screen.displayType) {
            DisplayType.ACTION_BAR -> {
                playerTextMap[player.uuid] = textComponent
            }
        }
    }
}
