package dev.zornov.repomine.entity.player

import dev.zornov.repomine.entity.NO_COLLISION_TEAM
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.worldseed.multipart.animations.AnimationHandlerImpl

class RepoPlayerMob(player: Player) : EntityCreature(EntityType.ZOMBIE) {
    val model = RepoPlayerModel().apply {
        init(player.instance, player.position)
    }
    val animationHandler = AnimationHandlerImpl(model)

    init {
        setInstance(player.instance, player.position).join()
        isInvisible = true
        team = NO_COLLISION_TEAM
        model.removeViewer(player)
    }

    override fun tick(time: Long) {
        super.tick(time)
        model.position = position
        model.setGlobalRotation(position.yaw.toDouble(), position.pitch.toDouble())
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        model.addViewer(player)
    }

    @Suppress("UnstableApiUsage")
    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        model.removeViewer(player)
    }

    override fun remove() {
        super.remove()
        model.destroy()
        animationHandler.destroy()
    }
}
