package dev.zornov.repomine.entity

import net.minestom.server.MinecraftServer
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.TeamBuilder

// Fuck minestom
private val manager = MinecraftServer.getTeamManager()

val NO_COLLISION_TEAM = TeamBuilder("no_collision", manager)
    .collisionRule(TeamsPacket.CollisionRule.NEVER)
    .build()