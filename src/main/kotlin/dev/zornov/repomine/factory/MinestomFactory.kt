
package dev.zornov.repomine.factory

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.instance.InstanceManager
import net.minestom.server.network.ConnectionManager
import net.minestom.server.timer.SchedulerManager

@Suppress("unused")
@Factory
class MinestomFactory {

    @Singleton
    @Context
    fun minecraftServer(): MinecraftServer {
        return MinecraftServer.init()
    }

    @Singleton
    fun instanceManager(minecraftServer: MinecraftServer): InstanceManager =
        MinecraftServer.getInstanceManager()

    @Singleton
    fun eventHandler(minecraftServer: MinecraftServer): GlobalEventHandler =
        MinecraftServer.getGlobalEventHandler()

    @Singleton
    fun connectionManager(minecraftServer: MinecraftServer): ConnectionManager =
        MinecraftServer.getConnectionManager()

    @Singleton
    fun commandManager(minecraftServer: MinecraftServer): CommandManager =
        MinecraftServer.getCommandManager()

    @Singleton
    fun scheduler(minecraftServer: MinecraftServer): SchedulerManager =
        MinecraftServer.getSchedulerManager()
}
