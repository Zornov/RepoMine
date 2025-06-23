package dev.zornov.repomine.entity.monster.apex.target

import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.ai.TargetSelector

class ApexPredatorTargetSelector(
    val entity: ApexPredatorEntity
) : TargetSelector(entity) {

    override fun findTarget(): Entity? {
        val instance = entity.instance ?: return null
        if (entity.isDead()) return null

        return instance.players
            .asSequence()
            .filter {
                it != entity &&
                        !it.isDead &&
                        !it.isInvisible &&
                        it.entityType == EntityType.PLAYER &&
                        it.position.distance(entity.position) <= 10
            }
            .minByOrNull { it.position.distance(entity.position) }
    }
}