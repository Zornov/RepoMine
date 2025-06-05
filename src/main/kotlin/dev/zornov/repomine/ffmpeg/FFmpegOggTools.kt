package dev.zornov.repomine.ffmpeg

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.FilterChain
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import jakarta.inject.Singleton
import net.kyori.adventure.key.Key
import java.nio.file.Path
import java.nio.file.Paths

@Singleton
class FFmpegOggTools(
    val ffmpeg: FFmpeg,
){


    class OggDsl {
        internal val inputs: MutableList<UrlInput> = mutableListOf()
        internal var filterChain: FilterChain? = null

        fun addInput(input: UrlInput) {
            inputs.add(input)
        }

        fun setFilter(chain: FilterChain) {
            filterChain = chain
        }
    }

    fun createOgg(
        key: Key,
        configure: OggDsl.() -> Unit
    ) {
        val outputFile: Path = Paths.get("${key.asMinimalString()}.ogg")

        val spec = OggDsl().apply(configure)


        for (input in spec.inputs) {
            ffmpeg.addInput(input)
        }

        spec.filterChain?.let { chain ->
            ffmpeg
                .addArgument("-af")
                .addArgument(chain.value)
        }

        ffmpeg
            .addArgument("-c:a")
            .addArgument("libvorbis")
            .addArgument(outputFile.toAbsolutePath().toString())
            .addArgument("-y")

        ffmpeg.execute()
    }
}
