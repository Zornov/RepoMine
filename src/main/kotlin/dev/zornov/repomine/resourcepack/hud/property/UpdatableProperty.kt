package dev.zornov.repomine.resourcepack.hud.property

import dev.zornov.repomine.resourcepack.hud.widget.HudWidget
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property delegate class that provides reactive updates to its associated `HudWidget` when the property value changes.
 *
 * @param T the type of the property value.
 * @property initialValue the initial value of the property.
 * @property widget the `HudWidget` associated with the property, which will handle updates.
 */
class UpdatableProperty<T>(
    var initialValue: T,
    val widget: HudWidget
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = initialValue

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (initialValue != value) {
            initialValue = value
            widget.onUpdate?.invoke()
        }
    }
}

/**
 * Creates an updatable property associated with the specified `HudWidget`.
 * The property provides reactive updates, ensuring the `HudWidget` is notified whenever the property's value changes.
 *
 * @param T the type of the property value.
 * @param initial the initial value of the property.
 * @return an instance of `UpdatableProperty` that binds the property to the `HudWidget`.
 */
fun <T> HudWidget.updatable(initial: T): UpdatableProperty<T> {
    return UpdatableProperty(initial, this)
}
