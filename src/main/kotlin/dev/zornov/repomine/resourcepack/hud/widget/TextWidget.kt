package dev.zornov.repomine.resourcepack.hud.widget

import dev.zornov.repomine.ext.bottomPadding
import dev.zornov.repomine.ext.horizontalPadding
import dev.zornov.repomine.ext.topPadding
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*
import kotlin.math.abs

data class TextWidget(
    var text: Component,
    var verticalPadding: Int = 0,
    var horizontalPadding: Int = 0
) : HudWidget() {

    val customData = hashMapOf<UUID, TextWidget>()

    fun modifyFor(player: Player, modification: TextWidget.() -> Unit) {
        customData[player.uuid] = this.copy().apply(modification)
    }

    override fun getComponent(player: UUID): Component {
        val effectiveWidget = customData[player] ?: this

        var padded = effectiveWidget.text

        padded = when {
            effectiveWidget.verticalPadding > 0 -> padded.topPadding(effectiveWidget.verticalPadding)
            effectiveWidget.verticalPadding < 0 -> padded.bottomPadding(abs(effectiveWidget.verticalPadding))
            else -> padded
        }

        return padded.horizontalPadding(effectiveWidget.horizontalPadding)
    }

}
