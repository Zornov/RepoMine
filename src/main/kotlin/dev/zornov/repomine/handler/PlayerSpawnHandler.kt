package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.MinestomEvent
import dev.zornov.repomine.entity.Minimal
import jakarta.inject.Singleton
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerSpawnEvent
import net.worldseed.multipart.animations.AnimationHandlerImpl

@Singleton
class PlayerSpawnHandler : MinestomEvent<PlayerSpawnEvent>() {

    override fun handle(event: PlayerSpawnEvent) {
        val player: Player = event.player
        player.gameMode = GameMode.CREATIVE


        // TODO: Move to entity handling in level space
        val model = Minimal()
        model.init(player.instance, player.position)

        model.addViewer(player)

        val animationHandler = AnimationHandlerImpl(model)
        animationHandler.playRepeat("dab")
    }
}
