package dev.zornov.repomine.resourcepack.hud.widget

import dev.zornov.repomine.ext.bottomPadding
import dev.zornov.repomine.ext.horizontalPadding
import dev.zornov.repomine.ext.topPadding
import dev.zornov.repomine.resourcepack.hud.property.updatable
import net.kyori.adventure.text.Component
import kotlin.math.abs

open class TextWidget(
    id: String,
    initialText: Component,
    verticalPadding: Int = 0,
    horizontalPadding: Int = 0
) : HudWidget(id) {

    /**
     * Represents the textual content of the `TextWidget`.
     * This property is mutable and its updates trigger the necessary UI refresh logic managed by the widget.
     *
     * The `text` can be dynamically updated to reflect changes in the widget's display.
     * Any modifications to this property, such as padding or content changes,
     * are applied during the rendering process in conjunction with other properties like `verticalPadding` and `horizontalPadding`.
     *
     * This property is bound to its containing `TextWidget` via an observable mechanism (`updatable`), which ensures
     * that any changes to the text are propagated to the relevant UI updates.
     */
    open var text: Component by updatable(initialText)
    open var verticalPadding: Int by updatable(verticalPadding)
    open var horizontalPadding: Int by updatable(horizontalPadding)

    /**
     * Applies vertical and horizontal padding to the `text` component and returns the resulting padded component.
     * The vertical padding is applied first, followed by the horizontal padding.
     *
     * Vertical padding adds space at the top or bottom of the text based on the sign of `verticalPadding`:
     * - A positive value adds padding at the top.
     * - A negative value adds padding at the bottom.
     **/
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
