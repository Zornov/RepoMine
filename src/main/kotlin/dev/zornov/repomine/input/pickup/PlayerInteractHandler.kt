package dev.zornov.repomine.input.pickup

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import dev.zornov.repomine.item.RepoItem
import dev.zornov.repomine.player.RepoPlayer
import jakarta.inject.Singleton
import net.minestom.server.event.player.PlayerEntityInteractEvent

@Singleton
class PlayerInteractHandler : EventListener {

    @EventHandler
    fun handle(event: PlayerEntityInteractEvent) {
        val parent = event.target.getTag(PARENT_TAG) ?: return
        val player = event.player as RepoPlayer

        if (player.currentItem == parent) {
            player.currentItem?.setNoGravity(false)
            player.currentItem = null
        } else {
            if (parent.isHolding) return
            player.currentItem = parent
            parent.setNoGravity(true)
        }
    }

    fun RepoItem.setNoGravity(noGravity: Boolean) {
        entity.setNoGravity(noGravity)
        proxy.setNoGravity(noGravity)
    }
}
