package dev.zornov.repomine.repo

import dev.zornov.repomine.ext.meta
import dev.zornov.repomine.input.pickup.PARENT_TAG
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.ItemDisplayMeta
import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

data class RepoItem(
    val material: Material,
    val initialPrice: Double
) {
    var price: Double = initialPrice

    val entity: Entity = Entity(EntityType.ITEM_DISPLAY).apply {
        meta<ItemDisplayMeta> {
            posRotInterpolationDuration = 2
            translation = Vec(0.0, 0.5, 0.0)
            itemStack = ItemStack.of(material)
        }
    }
    val proxy: Entity = Entity(EntityType.INTERACTION).apply {
        meta<InteractionMeta> {
            height = 0.8f
            width = 0.6f
        }
        setTag(PARENT_TAG, this@RepoItem)
    }


    fun spawnAt(instance: Instance, position: Pos) {
        entity.setInstance(instance, position)
        proxy.setInstance(instance, position)
    }

    fun moveTo(position: Pos) {
        entity.teleport(position)
        proxy.teleport(position.add(0.0, 0.0, 0.0))
    }

    fun despawn() {
        entity.remove()
        proxy.remove()
    }
}