package dev.zornov.repomine.entity.monster.apex.target

import dev.zornov.repomine.entity.monster.apex.ApexPredatorEntity
import net.minestom.server.entity.Entity
import net.minestom.server.entity.ai.TargetSelector

class ApexPredatorTargetSelector(
    val entity: ApexPredatorEntity
) : TargetSelector(entity) {

    override fun findTarget(): Entity? {
        if (entity.isDead()) return null

        return entity.model.viewers
            .asSequence()
            .filter {
                !it.isDead &&
                !it.isInvisible &&
                it.position.distance(entity.position) <= 10
            }
            .minByOrNull { it.position.distance(entity.position) }
    }
}