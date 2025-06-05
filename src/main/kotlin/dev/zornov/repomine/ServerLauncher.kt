package dev.zornov.repomine

import dev.zornov.repomine.audio.AudioPlayer
import dev.zornov.repomine.command.MpPlayCommand
import io.micronaut.context.annotation.Context
import jakarta.annotation.PostConstruct
import jakarta.inject.Singleton
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager

@Singleton
@Context
class ServerLauncher(
    val minecraftServer: MinecraftServer,
    val commandManager: CommandManager,
    val audioPlayer: AudioPlayer
) {
    @PostConstruct
    fun start() {
        minecraftServer.start("0.0.0.0", 25566)
        commandManager.register(MpPlayCommand(audioPlayer))
//        VelocityProxy.enable("r5H9K80Rc3XE")
    }
}