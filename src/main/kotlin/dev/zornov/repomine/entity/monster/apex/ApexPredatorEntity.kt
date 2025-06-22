package dev.zornov.repomine.entity.monster.apex

import dev.zornov.repomine.entity.NO_COLLISION_TEAM
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.instance.Instance
import net.worldseed.multipart.animations.AnimationHandlerImpl
import net.worldseed.multipart.events.ModelInteractEvent

class ApexPredatorEntity(inst: Instance, pos: Pos) : EntityCreature(EntityType.ZOMBIE) {
    private val model = ApexPredatorModel().apply { init(inst, pos) }
    private val animationHandler = AnimationHandlerImpl(model)
    var isAngry = false

    init {
        isInvisible = true
        team = NO_COLLISION_TEAM

        getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.05

        addAIGroup(
            listOf(
                ApexPredatorHuntGoal(this, animationHandler),
            ),
            listOf()
        )

        model.eventNode().addListener(ModelInteractEvent::class.java) {
            if (!isAngry) {
                animationHandler.stopRepeat("idle")
                animationHandler.playOnce("transform") {
                    isAngry = true
                    animationHandler.playRepeat("transform_idle")
                    getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.25
                }
            }
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
