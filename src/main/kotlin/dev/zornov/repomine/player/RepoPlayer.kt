package dev.zornov.repomine.player

import dev.zornov.repomine.config.SaleZoneConfig
import dev.zornov.repomine.ext.topPadding
import dev.zornov.repomine.repo.RepoItem
import dev.zornov.repomine.scene.RepoScene
import net.kyori.adventure.bossbar.BossBar
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
    lateinit var scene: RepoScene

    val bossbar = BossBar.bossBar(
        Component.text("test")
            .topPadding(50),
        1f,
        BossBar.Color.RED,
        BossBar.Overlay.PROGRESS
    )

    override fun spawn() {
        super.spawn()

        RepoItem(Material.DIAMOND, 100.0).apply {
            spawnAt(instance, Pos(1.0, 41.0, 2.0))
        }

        showBossBar(bossbar)

        scene = RepoScene(
            instance,
            SaleZoneConfig(
                start = Pos(12.0, 41.0, 1.0),
                end = Pos(10.0, 40.0, -1.0),
                labelPosition = Pos(12.2, 41.5, 0.5),
                labelRotation = Vec(-90.0, -20.0, 0.0)
            )
        ).also { it.create() }
    }

    override fun update(time: Long) {
        super.update(time)
        scene.update()

        val item = currentItem ?: return

        if (!item.isAlive) {
            currentItem = null
            return
        }

        val eyePos = position.add(0.0, eyeHeight, 0.0)
        val yawRad = Math.toRadians(position.yaw.toDouble())
        val pitchRad = Math.toRadians(position.pitch.toDouble())

        val direction = Vec(
            -sin(yawRad) * cos(pitchRad),
            -sin(pitchRad),
            cos(yawRad) * cos(pitchRad)
        ).normalize().mul(1.5)

        val target = eyePos.add(direction)
        val adjustedY = max(target.y - 0.4, position.y)

        if (instance.getBlock(target.withY(adjustedY)).isAir) {
            item.moveTo(
                target.withY(adjustedY)
                    .withYaw(0f)
                    .withPitch(0f)
            )
        }
    }
}
