package dev.zornov.repomine.collision

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

interface Collider {
    fun isInCollider(position: Point): Boolean
    fun isInCollider(entity: Entity): Boolean
    fun getEntityIn(instance: Instance): List<Entity>
}