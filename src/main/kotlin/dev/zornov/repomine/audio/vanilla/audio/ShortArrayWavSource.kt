package dev.zornov.repomine.audio.vanilla.audio

import dev.zornov.repomine.audio.vanilla.api.AudioSource
import org.quifft.QuiFFT
import org.quifft.output.FFTFrame
import org.quifft.output.FFTStream
import org.quifft.params.WindowFunction

class ShortArrayWavSource(pcmSamples: ShortArray) : AudioSource {
    val frames: List<AudioFrame>
    var currentIndex = 0

    val fftStream: FFTStream = QuiFFT(pcmSamples, 48_000f)
        .dBScale(false)
        .windowFunction(WindowFunction.HANNING)
        .fftStream()


    init {
        val tempList = mutableListOf<AudioFrame>()
        while (fftStream.hasNext()) {
            val nextFft: FFTFrame = fftStream.next()
            tempList.add(AudioFrame.fromQuiFFT(nextFft))
        }
        frames = tempList
    }

    override fun prepareNext(): Boolean {
        return currentIndex < frames.size
    }

    override fun getCurrent(): AudioFrame {
        if (!prepareNext()) {
            throw IllegalStateException("ShortArrayWavSource: all frames consumed")
        }
        return frames[currentIndex++]
    }
}
