package dev.zornov.repomine.resourcepack.hud.widget

import dev.zornov.repomine.ext.bottomPadding
import dev.zornov.repomine.ext.horizontalPadding
import dev.zornov.repomine.ext.topPadding
import dev.zornov.repomine.ext.withPerCharHorizontalPadding
import net.kyori.adventure.text.Component
import kotlin.math.abs

class TextWidget(
    override val id: String,
    val text: Component,
    val verticalPadding: Int = 0,
    val horizontalPadding: Int = 0
) : HudWidget {

    override fun getComponent(): Component {
        var padded = text

        padded = when {
            verticalPadding > 0 -> padded.topPadding(verticalPadding)
            verticalPadding < 0 -> padded.bottomPadding(abs(verticalPadding))
            else -> padded
        }

        return padded.horizontalPadding(horizontalPadding)
    }
}
