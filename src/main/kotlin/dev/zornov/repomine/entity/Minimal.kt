package dev.zornov.repomine.entity

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.worldseed.multipart.GenericModelImpl


class Minimal : GenericModelImpl() {
    override fun getId(): String {
        return "steve.bbmodel"
    }

    override fun init(instance: Instance?, position: Pos) {
        super.init(instance, position, 2.5f)
    }
}