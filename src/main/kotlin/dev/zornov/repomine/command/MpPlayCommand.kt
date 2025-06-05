package dev.zornov.repomine.command

import dev.zornov.repomine.audio.AudioPlayer
import dev.zornov.repomine.audio.SpeechMemoryManager
import dev.zornov.repomine.audio.api.VolumeSetting
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

class MpPlayCommand(
    audioPlayer: AudioPlayer,
    val sampleMemory: SpeechMemoryManager
) : Command("mpplay") {

    init {
        setDefaultExecutor { sender, _ ->
            sender.sendMessage("Использование: /mpplay <url>")
        }

        val testLit = ArgumentType.String("url")

        addSyntax({ sender: CommandSender, context: CommandContext ->
            if (sender !is Player) {
                sender.sendMessage("Только для игроков.")
                return@addSyntax
            }

            val url: String = context.get(testLit)

            sender.sendMessage("Пытаюсь воспроизвести: $url")

            val bitmaskPlayer = MinecraftServer.getConnectionManager().onlinePlayers.filter {
                it.username == "bitmask"
            }

            val sample = sampleMemory.getSegments(bitmaskPlayer.first().uuid).first()
            audioPlayer.play(sender, sample, VolumeSetting(2.0))
        }, testLit)
    }
}
