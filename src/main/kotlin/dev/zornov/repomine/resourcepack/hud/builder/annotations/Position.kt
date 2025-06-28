package dev.zornov.repomine.resourcepack.hud.builder.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Position(
    val vertical: Int = 0,
    val horizontal: Int = 0
)
