package dev.zornov.repomine.input.pickup

import dev.zornov.repomine.common.api.MinestomEvent
import dev.zornov.repomine.repo.RepoItem
import dev.zornov.repomine.player.RepoPlayer
import jakarta.inject.Singleton
import net.minestom.server.event.player.PlayerEntityInteractEvent

@Singleton
class PlayerInteractHandler : MinestomEvent<PlayerEntityInteractEvent>() {
    override fun handle(event: PlayerEntityInteractEvent) {
        val parent = event.target.getTag(PARENT_TAG) ?: return
        val player = event.player as RepoPlayer

        if (player.currentItem == parent) {
            player.currentItem?.setNoGravity(false)
            player.currentItem = null
        } else {
            player.currentItem = parent
            parent.setNoGravity(true)
        }
    }

    fun RepoItem.setNoGravity(noGravity: Boolean) {
        entity.setNoGravity(noGravity)
        proxy.setNoGravity(noGravity)
    }
}
