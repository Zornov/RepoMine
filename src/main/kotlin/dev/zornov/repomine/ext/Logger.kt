package dev.zornov.repomine.ext

import org.slf4j.Logger
import org.slf4j.event.Level
import kotlin.system.measureTimeMillis

inline fun Logger.recordTiming(
    level: Level,
    message: String,
    block: () -> Unit
) {
    val elapsed = measureTimeMillis(block)
    val text = "$message took ${elapsed}ms"
    when (level) {
        Level.TRACE -> this.trace(text)
        Level.DEBUG -> this.debug(text)
        Level.INFO  -> this.info(text)
        Level.WARN  -> this.warn(text)
        Level.ERROR -> this.error(text)
    }
}