package dev.zornov.repomine.command

import dev.zornov.repomine.audio.AudioPlayer
import dev.zornov.repomine.audio.VolumeSetting
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import java.io.File

class MpPlayCommand(
    audioPlayer: AudioPlayer,
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

            audioPlayer.play(sender, File("2.wav"), VolumeSetting(0.2))
        }, testLit)
    }
}
