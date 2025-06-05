package dev.zornov.repomine.repo

import dev.zornov.repomine.collision.BoxCollider
import dev.zornov.repomine.config.SaleZoneConfig
import dev.zornov.repomine.ext.meta
import dev.zornov.repomine.ext.toQuaternion
import dev.zornov.repomine.input.pickup.PARENT_TAG
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance

class SaleZone(
    val instance: Instance,
    minCorner: Point,
    maxCorner: Point,
    val labelPosition: Point,
    val labelRotation: Triple<Double, Double, Double>
) {
    constructor(instance: Instance, config: SaleZoneConfig) : this(
        instance,
        config.from,
        config.to,
        config.labelPosition,
        config.labelRotation
    )

    var currentPrice = 0.0
        private set

    val regionCollider = BoxCollider(
        minCorner.x(), minCorner.y(), minCorner.z(),
        maxCorner.x(), maxCorner.y(), maxCorner.z()
    )

    val priceDisplay = Entity(EntityType.TEXT_DISPLAY).apply {
        setNoGravity(true)
        meta<TextDisplayMeta> {
            text = Component.text("0.0")
            backgroundColor = 0
            scale = Vec(2.0, 2.0, 2.0)
            rightRotation = labelRotation.toQuaternion()
        }
    }

    fun spawn() = priceDisplay.setInstance(instance, labelPosition)

    fun update() {
        val price = regionCollider.getEntityIn(instance)
            .asSequence()
            .filter { it.entityType == EntityType.INTERACTION }
            .mapNotNull { it.getTag(PARENT_TAG) }
            .sumOf { it.price }

        currentPrice = price

        priceDisplay.meta<TextDisplayMeta> {
            text = Component.text(price)
        }
    }
}
