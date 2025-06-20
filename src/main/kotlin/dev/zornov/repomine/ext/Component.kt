package dev.zornov.repomine.ext

import dev.zornov.repomine.annotations.ShaderOnly
import net.kyori.adventure.text.Component
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
