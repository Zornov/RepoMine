package dev.zornov.repomine.entity.monster.apex

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.damage.EntityDamage
import net.worldseed.multipart.animations.AnimationHandlerImpl

class ApexPredatorHuntGoal(
    val entity: ApexPredatorEntity,
    val animationHandler: AnimationHandlerImpl
) : GoalSelector(entity) {

    var target: LivingEntity? = null
    var lastTargetPos: Pos = Pos.ZERO
    val attackRange = 2.5
    var lastAttackTime = 0L

    override fun shouldStart(): Boolean {
        return entity.isAngry
    }

    override fun start() {
        target = findNearestTarget()

        if (target != null) {
            entity.navigator.setPathTo(target!!.position)
            animationHandler.playRepeat("transform_walk")
        }
    }

    override fun tick(time: Long) {
        if (!entity.isAngry) return

        target = findNearestTarget()

        if (target != null) {
            val dist = entity.position.distance(target!!.position)
            if (!target!!.position.samePoint(lastTargetPos)) {
                lastTargetPos = target!!.position
                entity.navigator.setPathTo(lastTargetPos)
            }

            if (dist <= attackRange && System.currentTimeMillis() - lastAttackTime >= 2000L) {
                animationHandler.playOnce("transform_attack") {
                    lastAttackTime = System.currentTimeMillis()
                    target!!.damage(EntityDamage(entity, 1.5f))
                }
            }
        }
    }

    override fun shouldEnd(): Boolean {
        return !entity.isAngry
    }

    override fun end() {
        animationHandler.stopRepeat("transform_walk")
        target = null
    }

    fun findNearestTarget(): Player? {
        return entity.instance?.players
            ?.filter { it != entity && it.position.distance(entity.position) <= 10 }
            ?.minByOrNull { it.position.distance(entity.position) }
    }
}