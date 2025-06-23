package dev.zornov.repomine.entity.monster.apex.goal

import dev.zornov.repomine.entity.EntitySoundList
import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.ai.GoalSelector
import net.worldseed.multipart.animations.AnimationHandlerImpl
import java.time.Duration
import java.util.*

class ApexPredatorRandomGoal(
    val entity: ApexPredatorEntity,
    val animationHandler: AnimationHandlerImpl
) : GoalSelector(entity) {
    val random = Random()
    var lastWalkTime = 0L
    val walkCooldown = Duration.ofSeconds(10).toMillis()
    var lastSoundTime = 0L
    val soundInterval = 750L

    fun getNearbyOffsets(radius: Int) = buildList {
        for (x in -radius..radius)
            for (y in -2..2)
                for (z in -radius..radius)
                    if (x != 0 || y != 0 || z != 0)
                        add(Vec(x.toDouble(), y.toDouble(), z.toDouble()))
    }

    override fun shouldStart() =
        System.currentTimeMillis() - lastWalkTime >= walkCooldown &&
                animationHandler.playing == "idle" && !entity.isAngry

    override fun start() {
        val offsets = getNearbyOffsets(20)
        repeat(offsets.size) {
            val offset = offsets[random.nextInt(offsets.size)]
            if (entity.navigator.setPathTo(entity.position.add(offset))) {
                animationHandler.playRepeat("walk")
                return
            }
        }
    }

    override fun tick(time: Long) {
        val now = System.currentTimeMillis()
        if (animationHandler.playing == "walk" && now - lastSoundTime >= soundInterval) {
            entity.instance.playSound(EntitySoundList.Monster.ApexPredator.WALK, entity.position.x, entity.position.y, entity.position.z)
            lastSoundTime = now
        }
    }

    override fun shouldEnd(): Boolean =
        isCloseEnough(entity.position, entity.navigator.pathPosition, 2.5)

    override fun end() {
        animationHandler.stopRepeat("walk")
        lastWalkTime = System.currentTimeMillis()
    }

    fun isCloseEnough(pos1: Point, pos2: Point, threshold: Double = 0.5): Boolean {
        val dx = pos1.x() - pos2.x()
        val dy = pos1.y() - pos2.y()
        val dz = pos1.z() - pos2.z()
        return dx * dx + dy * dy + dz * dz < threshold * threshold
    }
}