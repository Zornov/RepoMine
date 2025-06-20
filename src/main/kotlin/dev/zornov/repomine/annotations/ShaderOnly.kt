package dev.zornov.repomine.annotations

/**
 * Marks API that requires shader support to produce a visual effect.
 * This is purely informational and has no effect at runtime.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ShaderOnly