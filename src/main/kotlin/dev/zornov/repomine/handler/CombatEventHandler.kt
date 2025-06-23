package dev.zornov.repomine.handler

import dev.zornov.repomine.common.api.EventHandler
import dev.zornov.repomine.common.api.EventListener
import jakarta.inject.Singleton
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.damage.EntityDamage
import net.minestom.server.event.entity.EntityAttackEvent

@Singleton
class CombatEventHandler : EventListener {
    @EventHandler
    fun handle(event: EntityAttackEvent) {
        (event.target as? LivingEntity)?.damage(
            EntityDamage.fromEntity(event.entity, 1f)
        )
    }
}