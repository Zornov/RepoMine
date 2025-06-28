package dev.zornov.repomine.resourcepack.hud.builder.annotations

import dev.zornov.repomine.resourcepack.hud.builder.DisplayType

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RenderType(val value: DisplayType)
