package dev.zornov.repomine.common.api

import io.micronaut.context.annotation.Prototype
import net.minestom.server.event.Event
import net.minestom.server.event.GlobalEventHandler
import java.lang.reflect.Method

@Prototype
interface EventListener {

    fun registerAll(handler: GlobalEventHandler): Int {
        val clazz = this::class.java

        var count = 0
        for (method in clazz.declaredMethods) {
            if (method.isAnnotationPresent(EventHandler::class.java)) {
                registerMethod(method, handler)
                count++
            }
        }
        return count
    }

    fun registerMethod(method: Method, handler: GlobalEventHandler) {
        val params = method.parameterTypes
        if (params.size != 1 || !Event::class.java.isAssignableFrom(params[0])) {
            throw IllegalArgumentException("Method ${method.name} must have exactly one Event parameter")
        }

        @Suppress("UNCHECKED_CAST")
        val eventType = params[0] as Class<out Event>

        handler.addListener(eventType) { event ->
            method.invoke(this, event)
        }
    }
}