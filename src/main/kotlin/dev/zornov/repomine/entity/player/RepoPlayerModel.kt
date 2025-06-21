package dev.zornov.repomine.entity.player

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.worldseed.multipart.GenericModelImpl

class RepoPlayerModel : GenericModelImpl() {
    override fun getId() = "player.bbmodel"

    override fun setPosition(pos: Pos?) {
        super.setPosition(pos?.add(0.0, 0.4, 0.0))
    }

    override fun init(instance: Instance?, position: Pos) {
        super.init(instance, position, 1.0f)
    }
}