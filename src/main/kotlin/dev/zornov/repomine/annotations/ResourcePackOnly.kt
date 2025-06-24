package dev.zornov.repomine.annotations

/**
 * Marks API that is only available when a resource pack is loaded.
 * This is purely informational and has no effect at runtime.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ResourcePackOnly