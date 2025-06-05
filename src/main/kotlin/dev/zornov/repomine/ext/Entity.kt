package dev.zornov.repomine.ext

import net.minestom.server.entity.Entity
import net.minestom.server.entity.metadata.EntityMeta

inline fun <reified M : EntityMeta> Entity.meta(noinline block: M.() -> Unit): Entity = apply {
    val meta = this.getEntityMeta() as? M
        ?: error("EntityMeta is not of type ${M::class.simpleName}")
    meta.setNotifyAboutChanges(false)
    meta.block()
    meta.setNotifyAboutChanges(true)
}