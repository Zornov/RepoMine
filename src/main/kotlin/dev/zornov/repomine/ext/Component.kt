package dev.zornov.repomine.ext

import dev.zornov.repomine.annotations.ShaderOnly
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.color.AlphaColor

@ShaderOnly
fun Component.topPadding(padding: Int): Component =
    color(TextColor.color(78, 90, padding.coerceIn(0, 255)))
        .shadowColor(AlphaColor(0, 0, 0, 0))