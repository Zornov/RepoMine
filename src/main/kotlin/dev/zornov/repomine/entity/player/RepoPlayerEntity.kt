package dev.zornov.repomine.entity.player

import dev.zornov.repomine.entity.NO_COLLISION_TEAM
import dev.zornov.repomine.entity.RepoEntity
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.timer.TaskSchedule
import kotlin.random.Random

class RepoPlayerEntity(player: Player) : RepoEntity(EntityType.PUFFERFISH) {
    override val model = RepoPlayerModel().also {
        it.init(player.instance, player.position)
    }

    init {
        setInstance(player.instance, player.position).join()
        isInvisible = true
        team = NO_COLLISION_TEAM
        playBlinkingAnimation()
    }

    fun playBlinkingAnimation() {
        val delay = Random.nextLong(5, 16)
        MinecraftServer.getSchedulerManager().buildTask {
            animationHandler.takeIf { it.repeating != "mouth_speak" }?.playOnce("eye_blink") {
                playBlinkingAnimation()
            } ?: playBlinkingAnimation()
        }.delay(TaskSchedule.seconds(delay)).schedule()
    }

    override fun tick(time: Long) {
        super.tick(time)
        model.position = position
        model.setGlobalRotation(position.yaw.toDouble(), position.pitch.toDouble())
    }

    override fun remove() {
        super.remove()
        model.destroy()
        animationHandler.destroy()
    }
}
