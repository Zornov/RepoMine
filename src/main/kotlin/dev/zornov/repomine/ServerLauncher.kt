package dev.zornov.repomine

import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer

@Singleton
@Context
class ServerLauncher(
    val minecraftServer: MinecraftServer
) : ApplicationEventListener<ServerStartupEvent> {

    override fun onApplicationEvent(event: ServerStartupEvent?) {
        minecraftServer.start("0.0.0.0", 25565)
    }
}