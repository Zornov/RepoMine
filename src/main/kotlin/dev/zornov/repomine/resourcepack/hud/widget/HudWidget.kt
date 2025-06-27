package dev.zornov.repomine.resourcepack.hud.widget

import net.kyori.adventure.text.Component
import java.util.*

abstract class HudWidget {
    var isVisible = true

    abstract fun getComponent(player: UUID): Component
}
