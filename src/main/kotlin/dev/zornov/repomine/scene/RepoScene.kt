package dev.zornov.repomine.scene

import dev.zornov.repomine.entity.RepoEntity
import dev.zornov.repomine.player.RepoPlayer

class RepoScene {
    private val players = mutableListOf<RepoPlayer>()
    private val monsters = mutableListOf<RepoEntity>()

    fun addPlayer(player: RepoPlayer) {
        players.forEach { p ->
            player.entity.show(p)
            p.entity.show(player)
            monsters.forEach { it.show(player) }
        }
        players.add(player)
    }

    fun removePlayer(player: RepoPlayer) {
        players.remove(player)
        players.forEach { p ->
            p.entity.hide(player)
            player.entity.hide(p)
            monsters.forEach { it.hide(player) }
        }
    }

    fun addMonster(monster: RepoEntity) {
        monsters.add(monster)
        players.forEach { monster.show(it) }
    }

    fun removeMonster(monster: RepoEntity) {
        monsters.remove(monster)
        players.forEach { monster.hide(it) }
    }
}