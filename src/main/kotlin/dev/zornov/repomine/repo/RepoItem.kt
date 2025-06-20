package dev.zornov.repomine.repo

import dev.zornov.repomine.ext.meta
import dev.zornov.repomine.input.pickup.PARENT_TAG
import dev.zornov.repomine.math.RayTrace
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.ItemDisplayMeta
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

data class RepoItem(val material: Material, val initialPrice: Double) {
    var price = initialPrice
    var isAlive = true
        private set
    var isHolding = false
    var lastPosition: Pos? = null
        private set

    val entity = ItemEntity()
    val proxy = Entity(EntityType.INTERACTION).apply {
        meta<InteractionMeta> {
            height = 0.8f; width = 0.6f
        }
        setBoundingBox(0.6, 0.8, 0.2)
        setTag(PARENT_TAG, this@RepoItem)
    }

    inner class ItemEntity : Entity(EntityType.ITEM_DISPLAY) {
        init {
            meta<ItemDisplayMeta> {
                posRotInterpolationDuration = 2
                translation = Vec(0.0, 0.5, 0.0)
                itemStack = ItemStack.of(material)
            }
        }

        override fun teleport(pos: Pos) = super.teleport(pos).also {
            val prev = lastPosition
            lastPosition = pos
            it.thenRunAsync {
                if (prev != null && RayTrace.hasObstruction(instance, prev, pos)) decreasePrice()
            }
        }
    }

    fun decreasePrice() {
        if (!isAlive) return
        price -= initialPrice * 0.05
        if (price <= 0) despawn()
    }

    fun spawnAt(inst: Instance, pos: Pos) {
        isAlive = true
        entity.setInstance(inst, pos)
        proxy.setInstance(inst, pos)
        lastPosition = pos
    }

    fun moveTo(pos: Pos) {
        entity.teleport(pos)
        proxy.teleport(pos)
    }

    fun despawn() {
        if (!isAlive) return
        isAlive = false
        entity.remove()
        proxy.remove()
    }
}
