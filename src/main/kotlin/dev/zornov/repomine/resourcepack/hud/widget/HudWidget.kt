package dev.zornov.repomine.resourcepack.hud.widget

import net.kyori.adventure.text.Component

abstract class HudWidget(
    val id: String
) {
    var onUpdate: (() -> Unit)? = null

    abstract fun getComponent(): Component
}
