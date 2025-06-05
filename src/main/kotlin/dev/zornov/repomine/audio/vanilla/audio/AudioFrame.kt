package dev.zornov.repomine.audio.vanilla.audio

import org.quifft.output.FFTFrame

data class AudioFrame(
    val frameStartMs: Double,
    val frameEndMs: Double,
    val bins: List<FrequencyBin>
) {
    data class FrequencyBin(val frequency: Double, val amplitude: Double)

    companion object {
        fun fromQuiFFT(frame: FFTFrame): AudioFrame {
            val bins = frame.bins.map { FrequencyBin(it.frequency, it.amplitude) }
            return AudioFrame(frame.frameStartMs, frame.frameEndMs, bins)
        }
    }
}