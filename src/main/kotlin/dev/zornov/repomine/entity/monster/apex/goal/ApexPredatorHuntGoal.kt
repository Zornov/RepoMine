package dev.zornov.repomine.entity.monster.apex.goal

import dev.zornov.repomine.entity.EntitySoundList
import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.damage.EntityDamage
import net.worldseed.multipart.animations.AnimationHandlerImpl

class ApexPredatorHuntGoal(
    val entity: ApexPredatorEntity,
    val animationHandler: AnimationHandlerImpl
) : GoalSelector(entity) {
    var lastTargetPos = Pos.ZERO
    var lastAttack = 0L
    var attacking = false

    val attackRange = 2.5
    val attackCooldown = 2000L

    override fun shouldStart() = entity.isAngry

    override fun start() {
        findTarget()?.also {
            entity.target = it
            entity.navigator.setPathTo(it.position)
            animationHandler.playRepeat("transform_walk")
        }
    }

    override fun tick(time: Long) {
        if (!entity.isAngry) return

        val target = findTarget() ?: return resetState().also { entity.target = null }
        entity.target = target

        if (!target.position.samePoint(lastTargetPos)) {
            lastTargetPos = target.position
            entity.navigator.setPathTo(lastTargetPos)
        }

        val now = System.currentTimeMillis()
        if (entity.position.distance(target.position) <= attackRange &&
            !attacking && now - lastAttack >= attackCooldown
        ) {
            (target as? LivingEntity)?.let { performAttack(it) }
        }
    }

    fun performAttack(target: LivingEntity) {
        attacking = true
        entity.model.viewers.forEach {
            it.playSound(EntitySoundList.Monster.ApexPredator.ATTACK, entity.position)
        }
        animationHandler.playOnce("transform_attack") {
            lastAttack = System.currentTimeMillis()
            target.damage(EntityDamage(entity, 1.5f))
            attacking = false
        }
    }

    override fun shouldEnd() = !entity.isAngry

    override fun end() {
        animationHandler.stopRepeat("transform_walk")
        entity.target = null
    }

    fun resetState() {
        entity.isAngry = false
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.05
        animationHandler.stopRepeat("transform_idle")
        animationHandler.playRepeat("idle")
    }
}