package dev.zornov.repomine.entity

import dev.zornov.repomine.ext.toKey
import net.kyori.adventure.sound.Sound

object EntitySoundList {
    fun sound(name: String) = Sound.sound(name.toKey(), Sound.Source.MASTER, 1f, 1f)

    object Monster {
        object ApexPredator {
            val ATTACK = sound("monster_duck")
            val WALK = sound("squeeze")
        }
    }
}