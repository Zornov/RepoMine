package dev.zornov.repomine.entity.monster.apex.goal

import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import net.minestom.server.entity.ai.GoalSelector
import net.worldseed.multipart.animations.AnimationHandlerImpl

class ApexPredatorRandomGoal(
    val entity: ApexPredatorEntity,
    val animationHandler: AnimationHandlerImpl
) : GoalSelector(entity) {
    override fun shouldStart() = !entity.isAngry

    override fun start() {
    }

    override fun tick(time: Long) {
        if (entity.isAngry) return
    }

    override fun shouldEnd() = entity.isAngry

    override fun end() {

    }
}