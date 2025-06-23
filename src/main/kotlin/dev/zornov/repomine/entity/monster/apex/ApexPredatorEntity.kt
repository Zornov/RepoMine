package dev.zornov.repomine.entity.monster.apex

import dev.zornov.repomine.entity.EntitySoundList
import dev.zornov.repomine.entity.NO_COLLISION_TEAM
import dev.zornov.repomine.entity.monster.apex.goal.ApexPredatorHuntGoal
import dev.zornov.repomine.entity.monster.apex.goal.ApexPredatorRandomGoal
import dev.zornov.repomine.entity.monster.apex.target.ApexPredatorTargetSelector
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.instance.Instance
import net.worldseed.multipart.animations.AnimationHandlerImpl
import net.worldseed.multipart.events.ModelDamageEvent
import net.worldseed.multipart.events.ModelInteractEvent
import java.time.Duration

class ApexPredatorEntity(inst: Instance, pos: Pos) : EntityCreature(EntityType.PUFFERFISH) {
    val model = ApexPredatorModel().apply { init(inst, pos) }
    val animationHandler = AnimationHandlerImpl(model)
    var isAngry = false

    init {
        isInvisible = true
        team = NO_COLLISION_TEAM
        getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.05

        addAIGroup(
            listOf(
                ApexPredatorHuntGoal(this, animationHandler),
                ApexPredatorRandomGoal(this, animationHandler)
            ),
            listOf(ApexPredatorTargetSelector(this))
        )

        fun triggerAngry() {
            if (!isAngry) {
                animationHandler.stopRepeat("idle")
                animationHandler.playOnce("transform") {
                    isAngry = true
                    animationHandler.playRepeat("transform_idle")
                    getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.4
                    instance.playSound(
                        EntitySoundList.Monster.ApexPredator.ATTACK,
                        position.x, position.y, position.z
                    )
                    MinecraftServer.getSchedulerManager().buildTask {
                        isAngry = false
                        animationHandler.stopRepeat("transform_idle")
                        animationHandler.playRepeat("idle")
                        getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.05
                    }.delay(Duration.ofSeconds(15)).schedule()
                }
            }
        }

        model.eventNode().apply {
            addListener(ModelInteractEvent::class.java) { triggerAngry() }
            addListener(ModelDamageEvent::class.java) { triggerAngry() }
        }

        animationHandler.playRepeat("idle")
        setInstance(inst, pos).join()
    }

    override fun tick(time: Long) {
        super.tick(time)
        if (!isDead) {
            model.position = position
            model.setGlobalRotation(position.yaw.toDouble(), position.pitch.toDouble())
        }
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