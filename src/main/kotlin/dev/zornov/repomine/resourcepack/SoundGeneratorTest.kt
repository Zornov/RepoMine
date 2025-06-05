package dev.zornov.repomine.resourcepack

import com.github.kokorin.jaffree.ffmpeg.FilterChain
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import dev.zornov.repomine.ffmpeg.FFmpegOggTools
import jakarta.inject.Singleton
import net.kyori.adventure.key.Key


@Singleton
class SoundGeneratorTest(
    val ffmpegOggTools: FFmpegOggTools,
) {

    val durationSec = 40 / 1000.0
    val fadeSec = 20 / 1000.0


    fun createSine(key: Key, freq: Double) = ffmpegOggTools.createOgg(key) {
        addInput(
            UrlInput
                .fromUrl("sine=frequency=$freq:duration=$durationSec")
                .setFormat("lavfi")
        )
        setFilter(
            FilterChain.of(
                { "volume=12.0f" },
                { "afade=t=in:st=0:d=$fadeSec" },
                { "afade=t=out:st=${durationSec - fadeSec}:d=$fadeSec" }
            )
        )
    }
}