package dev.zornov.repomine.player

import dev.zornov.repomine.entity.NO_COLLISION_TEAM
import dev.zornov.repomine.entity.player.RepoPlayerEntity
import dev.zornov.repomine.item.RepoItem
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class RepoPlayer(
    connection: PlayerConnection,
    profile: GameProfile
) : Player(connection, profile) {

    var currentItem: RepoItem? = null
        set(value) {
            field?.isHolding = false
            field = value?.apply { isHolding = true }
        }

    lateinit var entity: RepoPlayerEntity
        private set

    override fun spawn() {
        super.spawn()
        isAutoViewable = false
        team = NO_COLLISION_TEAM
        RepoItem(Material.DIAMOND, 100).spawnAt(instance, Pos(1.0, 41.0, 2.0))
//        entity = RepoPlayerEntity(this)
    }

    override fun update(time: Long) {
        super.update(time)
        currentItem?.takeIf { it.isAlive }?.let { item ->
            moveItemTowardLook(item)
        } ?: showHoveredEntityPrice()
        entity.teleport(position.withPitch(0f))
    }

    fun showHoveredEntityPrice() {
        sendActionBar(Component.empty())
    }

    fun moveItemTowardLook(item: RepoItem) {
        val eye = position.add(0.0, eyeHeight, 0.0)
        val yawRad = Math.toRadians(position.yaw.toDouble())
        val pitchRad = Math.toRadians(position.pitch.toDouble())
        val dir = Vec(
            -sin(yawRad) * cos(pitchRad),
            -sin(pitchRad),
            cos(yawRad) * cos(pitchRad)
        ).normalize().mul(1.5)
        val target = eye.add(dir)
        val y = max(target.y - 0.4, position.y)
        if (instance.getBlock(target.withY(y)).isAir) {
            item.moveTo(target.withY(y).withYaw(0f).withPitch(0f))
        }
    }

    override fun remove() {
        super.remove()
        entity.remove()
    }
}