package dev.zornov.repomine.resourcepack.hud.builder

import dev.zornov.repomine.resourcepack.hud.builder.annotations.RenderType
import kotlin.reflect.full.findAnnotation

abstract class HudScreen {
    val displayType by lazy {
        this::class.findAnnotation<RenderType>()?.value
            ?: error("HudScreenDefinition annotation missing on ${this::class.simpleName}")
    }
}