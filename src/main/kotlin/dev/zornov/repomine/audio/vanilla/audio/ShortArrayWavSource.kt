package dev.zornov.repomine.audio.vanilla.audio

import org.quifft.QuiFFT
import org.quifft.output.FFTFrame
import org.quifft.output.FFTStream
import org.quifft.params.WindowFunction
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class ShortArrayWavSource(pcmSamples: ShortArray) : AudioSource {

    private var audioFrame: AudioFrame? = null

    private var consumed = false

    init {
        val tempWav = File.createTempFile("plasmo_voice_chunk_", ".wav").apply {
            deleteOnExit()
        }
        try {
            writeShortArrayAsWav48kHzMono(pcmSamples, tempWav)

            val fftStream: FFTStream = QuiFFT(tempWav)
                .windowFunction(WindowFunction.HANNING)
                .dBScale(false)
                .fftStream()

            if (fftStream.hasNext()) {
                val firstFft: FFTFrame = fftStream.next()
                audioFrame = AudioFrame.fromQuiFFT(firstFft)
            } else {
                audioFrame = null
            }
        } finally {
            tempWav.delete()
        }
    }

    override fun prepareNext(): Boolean {
        return !consumed && audioFrame != null
    }

    override fun getCurrent(): AudioFrame {
        if (consumed) {
            throw IllegalStateException("ShortArrayWavSource: already consumed")
        }
        consumed = true
        return audioFrame ?: throw IllegalStateException("No AudioFrame was produced")
    }

    @Throws(IOException::class)
    fun writeShortArrayAsWav48kHzMono(pcm: ShortArray, outFile: File) {
        val audioFormat = AudioFormat(
            /* sampleRate  = */ 48_000f,
            /* sampleSizeInBits = */ 16,
            /* channels = */ 1,
            /* signed = */ true,
            /* bigEndian = */ false
        )

        val byteBuffer = ByteArray(pcm.size * 2)
        var bi = 0
        for (s in pcm) {
            byteBuffer[bi++] = (s.toInt() and 0xFF).toByte()
            byteBuffer[bi++] = ((s.toInt() shr 8) and 0xFF).toByte()
        }

        val bais = ByteArrayInputStream(byteBuffer)
        val frameLength = pcm.size.toLong()
        val audioStream = AudioInputStream(bais, audioFormat, frameLength)

        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outFile)

        audioStream.close()
        bais.close()
    }
}