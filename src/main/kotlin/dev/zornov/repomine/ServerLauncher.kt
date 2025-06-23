package dev.zornov.repomine

import io.micronaut.context.annotation.Context
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer

@Singleton
@Context
class ServerLauncher(
    val minecraftServer: MinecraftServer
) {
    @PostConstruct
    fun start() {
        minecraftServer.start("0.0.0.0", 25565) // TODO: Move to config
//        VelocityProxy.enable("r5H9K80Rc3XE")
    }
}