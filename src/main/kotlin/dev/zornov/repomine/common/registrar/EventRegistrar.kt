package dev.zornov.repomine.common.registrar

import dev.zornov.repomine.common.api.MinestomEvent
import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import jakarta.inject.Singleton
import net.minestom.server.event.GlobalEventHandler
import org.slf4j.Logger
import kotlin.system.measureTimeMillis

@Singleton
@Context
class EventRegistrar(
    @Suppress("MnInjectionPoints") val beans: List<MinestomEvent<*>>,
    val eventHandler: GlobalEventHandler,
    val logger: Logger
) : ApplicationEventListener<StartupEvent> {

    override fun onApplicationEvent(event: StartupEvent) {
        var count = 0
        val time = measureTimeMillis {
            beans.forEach {
                it.register(eventHandler)
                count++
            }
        }
        logger.info("Registered $count events in %.2f ms".format(time / 1000.0))
    }
}