package dev.zornov.repomine.resourcepack.hud.widget

import dev.zornov.repomine.resourcepack.hud.property.updatable
import net.kyori.adventure.text.Component

abstract class HudWidget(
    val id: String
) {
    var isVisible by updatable(true)

    abstract fun getComponent(): Component


    var onUpdate: (() -> Unit)? = null
}
