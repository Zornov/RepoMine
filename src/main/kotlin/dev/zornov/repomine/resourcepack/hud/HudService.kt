package dev.zornov.repomine.resourcepack.hud

import dev.zornov.repomine.resourcepack.hud.widget.HudWidget
import jakarta.inject.Singleton
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

@Singleton
class HudService {
    val globalHudComponents = mutableSetOf<HudWidget>()

    val bossBar: BossBar = BossBar.bossBar(
        Component.empty(), 1f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS
    )

    fun updateBossBar() {
        bossBar.name(Component.text().apply { component ->
            globalHudComponents.forEach { component.append(it.getComponent()) }
        }.build())
    }

    fun addGlobalHudComponent(widget: HudWidget): Boolean =
        globalHudComponents.add(widget).also { if (it) updateBossBar() }

    inline fun <reified T : HudWidget> getGlobalHudComponent(id: String): T? =
        globalHudComponents.firstOrNull { it.id == id && it is T } as? T
}
