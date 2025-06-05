package dev.zornov.repomine.collision

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import kotlin.math.max
import kotlin.math.min

data class BoxCollider(
    val x1: Double,
    val y1: Double,
    val z1: Double,
    val x2: Double,
    val y2: Double,
    val z2: Double
) : Collider {
    override fun isInCollider(position: Point): Boolean {
        val minX = min(x1, x2)
        val maxX = max(x1, x2)
        val minY = min(y1, y2)
        val maxY = max(y1, y2)
        val minZ = min(z1, z2)
        val maxZ = max(z1, z2)
        return position.x() in minX..maxX && position.y() in minY..maxY && position.z() in minZ..maxZ
    }

    override fun isInCollider(entity: Entity): Boolean {
        val position = entity.position
        return isInCollider(position)
    }

    override fun getEntityIn(instance: Instance): List<Entity> {
        return instance.entities.filter { isInCollider(it) }
    }
}