package dev.zornov.repomine.factory

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import java.nio.file.Paths

@Factory
class FFmpegFactory {

    @Singleton
    fun ffmpeg(): FFmpeg {
        val ffmpeg = FFmpeg.atPath(Paths.get("bin/ffmpeg"))

        ffmpeg.setOverwriteOutput(true)

        return ffmpeg
    }
}