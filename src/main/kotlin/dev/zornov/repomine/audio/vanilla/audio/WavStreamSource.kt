package dev.zornov.repomine.audio.vanilla.audio

import dev.zornov.repomine.audio.vanilla.api.AudioSource
import org.quifft.QuiFFT
import org.quifft.output.FFTFrame
import org.quifft.output.FFTStream
import org.quifft.params.WindowFunction
import java.io.File

class WavStreamSource(file: File) : AudioSource {
    val stream: FFTStream = QuiFFT(file)
        .windowFunction(WindowFunction.RECTANGULAR)
        .dBScale(false)
        .fftStream()

    var currentFrame: FFTFrame? = null

    override fun prepareNext(): Boolean {
        return if (stream.hasNext()) {
            currentFrame = stream.next()
            true
        } else {
            false
        }
    }

    override fun getCurrent(): AudioFrame {
        val f = currentFrame ?: throw IllegalStateException("Нет текущего фрейма. Сначала вызовите prepareNext().")
        return AudioFrame.fromQuiFFT(f)
    }
}