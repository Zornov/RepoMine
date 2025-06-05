package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.MinestomEvent
import dev.zornov.repomine.resourcepack.SoundGeneratorTest
import jakarta.inject.Singleton
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.timer.SchedulerManager

@Singleton
class PlayerSpawnHandler(
    val test: SoundGeneratorTest,
    val scheduler: SchedulerManager
) : MinestomEvent<PlayerSpawnEvent>() {

    var tickCounter: Long = 0

    override fun handle(event: PlayerSpawnEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.CREATIVE
        

    }
}
