package dev.zornov.repomine.audio.api

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException

object WavDecoder {
    @Throws(IOException::class, UnsupportedAudioFileException::class)
    fun decodeWavToPcm(file: File): ShortArray {
        val aisOriginal: AudioInputStream = AudioSystem.getAudioInputStream(file)
        val sourceFormat = aisOriginal.format

        val targetFormat = AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            48_000f,
            16,
            sourceFormat.channels,
            sourceFormat.channels * 2,
            48_000f,
            false
        )

        val ais: AudioInputStream = if (AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
            AudioSystem.getAudioInputStream(targetFormat, aisOriginal)
        } else {
            throw UnsupportedAudioFileException(
                "Cannot convert format ${sourceFormat.sampleRate} Hz, " +
                        "${sourceFormat.sampleSizeInBits}-bit to 48 kHz 16-bit PCM."
            )
        }

        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (ais.read(buffer).also { bytesRead = it } != -1) {
            baos.write(buffer, 0, bytesRead)
        }

        val audioBytes = baos.toByteArray()
        ais.close()
        aisOriginal.close()

        val totalSamples = audioBytes.size / 2
        val samples = ShortArray(totalSamples)
        var bi = 0
        var si = 0

        while (bi + 1 < audioBytes.size) {
            val low = audioBytes[bi].toInt() and 0xFF
            val high = (audioBytes[bi + 1].toInt() shl 8)
            samples[si] = (low or high).toShort()
            si++
            bi += 2
        }

        return samples
    }
}