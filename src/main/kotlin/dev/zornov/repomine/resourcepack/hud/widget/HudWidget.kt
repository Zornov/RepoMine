package dev.zornov.repomine.resourcepack.hud.widget

import net.kyori.adventure.text.Component

interface HudWidget {
    val id: String
    fun getComponent(): Component
}
