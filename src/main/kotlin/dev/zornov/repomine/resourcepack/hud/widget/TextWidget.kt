package dev.zornov.repomine.resourcepack.hud.widget

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

data class TextWidget(
    var text: Component
) : HudWidget() {

    val customData = hashMapOf<UUID, TextWidget>()

    fun modifyFor(player: Player, modification: TextWidget.() -> Unit) {
        customData[player.uuid] = this.copy().apply(modification)
    }

    override fun getComponent(player: UUID): Component {
        return customData[player]?.text ?: text
    }
}
