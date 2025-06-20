package dev.zornov.repomine.math

import net.minestom.server.coordinate.Point
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block

object RayTrace {

    fun hasObstruction(
        instance: Instance,
        start: Point,
        end: Point,
        step: Double = 0.1,
        predicate: (block: Block) -> Boolean = { !it.isAir }
    ): Boolean {
        val direction = end.sub(start)
        val distance = start.distance(end)
        var traveled = 0.0

        while (traveled <= distance) {
            val pos = start.add(direction.mul(traveled))
            val block = instance.getBlock(pos)
            if (predicate(block)) {
                return true
            }
            traveled += step
        }
        return false
    }
}