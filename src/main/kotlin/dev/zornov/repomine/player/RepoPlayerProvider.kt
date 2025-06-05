package dev.zornov.repomine.player

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import jakarta.inject.Singleton
import net.minestom.server.entity.Player
import net.minestom.server.network.ConnectionManager
import net.minestom.server.network.PlayerProvider
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import org.slf4j.Logger

@Singleton
class RepoPlayerProvider(
    val connectionManager: ConnectionManager,
    val logger: Logger
) : PlayerProvider, ApplicationEventListener<StartupEvent> {

    override fun onApplicationEvent(event: StartupEvent) {
        connectionManager.setPlayerProvider(this)
        logger.info("Registered RepoPlayerProvider on startup")
    }

    override fun createPlayer(
        connection: PlayerConnection,
        gameProfile: GameProfile
    ): Player {
        return RepoPlayer(connection, gameProfile)
    }
}