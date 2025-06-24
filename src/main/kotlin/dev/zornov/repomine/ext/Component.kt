package dev.zornov.repomine.ext

import dev.zornov.repomine.annotations.ResourcePackOnly
import dev.zornov.repomine.annotations.ShaderOnly
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.color.AlphaColor

@ShaderOnly
fun Component.topPadding(padding: Int): Component {
    require(padding in 0..255) { "Padding must be in range 0..255, got $padding" }
    return color(TextColor.color(78, 90, padding))
        .shadowColor(AlphaColor(0, 0, 0, 0))
}

@ShaderOnly
fun Component.bottomPadding(padding: Int): Component {
    require(padding in 0..255) { "Padding must be in range 0..255, got $padding" }
    return color(TextColor.color(79, 90, padding))
        .shadowColor(AlphaColor(0, 0, 0, 0))
}

@ResourcePackOnly
fun Component.horizontalPadding(offset: Int): Component {
    require(offset in -20..20) { "Offset must be in range -20..20, got $offset" }

    val char = (0xE000 + (offset + 20)).toChar()
    return append(Component.text(char))
}

@ResourcePackOnly
fun Component.withPerCharHorizontalPadding(offset: Int): Component {
    require(offset in -20..20) { "Offset must be in range -20..20, got $offset" }

    val builder = Component.text().style(this.style())

    val content = (this as? TextComponent)?.content() ?: return this

    for (char in content) {
        builder.append(Component.text(char.toString(), this.style()).horizontalPadding(offset))
    }

    return builder.build()
}