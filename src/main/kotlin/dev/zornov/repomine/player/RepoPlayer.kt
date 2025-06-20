package dev.zornov.repomine.player

import dev.zornov.repomine.config.SaleZoneConfig
import dev.zornov.repomine.ext.bottomPadding
import dev.zornov.repomine.input.pickup.PARENT_TAG
import dev.zornov.repomine.repo.RepoItem
import dev.zornov.repomine.scene.RepoScene
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.network.player.GameProfile
import net.minestom.server.network.player.PlayerConnection
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class RepoPlayer(connection: PlayerConnection, profile: GameProfile) : Player(connection, profile) {

    var currentItem: RepoItem? = null
    lateinit var scene: RepoScene

    override fun spawn() {
        super.spawn()
        // TODO: Test section, only for test
        RepoItem(Material.DIAMOND, 100.0).spawnAt(instance, Pos(1.0, 41.0, 2.0))
        scene = RepoScene(instance, SaleZoneConfig(
            start = Pos(12.0, 41.0, 1.0),
            end = Pos(10.0, 40.0, -1.0),
            labelPosition = Pos(12.2, 41.5, 0.5),
            labelRotation = Vec(-90.0, -20.0, 0.0)
        )).apply { create() }
    }

    override fun update(time: Long) {
        super.update(time)
        scene.update()

        currentItem?.takeIf { it.isAlive }?.let {
            moveItemTowardLook(it)
            showPrice(it)
        } ?: run {
            getLineOfSightEntity(5.0) { it.entityType == EntityType.INTERACTION }
                ?.getTag(PARENT_TAG)
                ?.let { sendActionBar(Component.text(it.price).bottomPadding(80)) }
                ?: sendActionBar(Component.empty())
        }
    }

    fun showPrice(item: RepoItem) {
        sendActionBar(Component.text(item.price).bottomPadding(80))
    }

    fun moveItemTowardLook(item: RepoItem) {
        val eyePos = position.add(0.0, eyeHeight, 0.0)
        val (yaw, pitch) = Math.toRadians(position.yaw.toDouble()) to Math.toRadians(position.pitch.toDouble())

        val direction = Vec(
            -sin(yaw) * cos(pitch),
            -sin(pitch),
            cos(yaw) * cos(pitch)
        ).normalize().mul(1.5)

        val target = eyePos.add(direction)
        val adjustedY = max(target.y - 0.4, position.y)

        if (instance.getBlock(target.withY(adjustedY)).isAir) {
            item.moveTo(target.withY(adjustedY).withYaw(0f).withPitch(0f))
        }
    }
}
