package dev.zornov.repomine.common.api

import io.micronaut.context.annotation.Prototype
import net.minestom.server.event.Event
import net.minestom.server.event.GlobalEventHandler
import java.lang.reflect.ParameterizedType

@Prototype
abstract class MinestomEvent<T : Event> {
    abstract fun handle(event: T)

    fun register(handler: GlobalEventHandler) {
        @Suppress("UNCHECKED_CAST")
        val clazz = (javaClass.genericSuperclass as? ParameterizedType)
            ?.actualTypeArguments?.get(0) as? Class<T>
            ?: throw IllegalStateException("Unknown event type for ${javaClass.name}")

        handler.addListener(clazz) { handle(it) }
    }
}
