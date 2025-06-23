package dev.zornov.repomine.entity

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.worldseed.multipart.GenericModel
import net.worldseed.multipart.animations.AnimationHandlerImpl

abstract class RepoEntity(
    entityType: EntityType
) : EntityCreature(entityType) {
    abstract val model: GenericModel
    val animationHandler by lazy { AnimationHandlerImpl(model) }

    fun show(player: Player) {
        model.addViewer(player)
        this.addViewer(player)
    }

    fun hide(player: Player) {
        model.removeViewer(player)
        this.removeViewer(player)
    }
}