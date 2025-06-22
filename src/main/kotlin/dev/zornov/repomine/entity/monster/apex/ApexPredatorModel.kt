package dev.zornov.repomine.entity.monster.apex

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.worldseed.multipart.GenericModelImpl

class ApexPredatorModel : GenericModelImpl() {
    override fun getId() = "monster/apex_predator.bbmodel"

    override fun init(instance: Instance?, position: Pos) {
        super.init(instance, position, 1.0f)
    }
}