package dev.zornov.repomine.world

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.utils.time.TimeUnit

@Singleton
class WorldManager(
    val instanceManager: InstanceManager,
    val scheduler: SchedulerManager
) : EventListener, ApplicationEventListener<ServerStartupEvent> {

    lateinit var world: InstanceContainer
        private set

    override fun onApplicationEvent(event: ServerStartupEvent?) {
        world = instanceManager.createInstanceContainer()
        world.chunkLoader = AnvilLoader("./tmp/worlds")
        world.setGenerator { it.modifier().fillHeight(0, 40, Block.GRASS_BLOCK) }
        world.setChunkSupplier(::LightingChunk)

        scheduler.buildTask {
            world.saveChunksToStorage()
        }.repeat(60, TimeUnit.SECOND).schedule()

        scheduler.buildShutdownTask {
            world.saveChunksToStorage()
        }

        world.setTime(1_000L)
        world.setTimeRate(0)
    }

    @EventHandler
    fun handle(event: AsyncPlayerConfigurationEvent) {
        event.spawningInstance = world
        event.player.respawnPoint = Pos(0.0, 40.0, 0.0)
    }

    @PreDestroy
    fun shutdown() {
        world.saveChunksToStorage()
    }

}
