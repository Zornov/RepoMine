package dev.zornov.repomine.audio.vanilla

import net.minestom.server.sound.SoundEvent
import java.util.*
import kotlin.math.ln

object SinewaveRegistry {
    val freqToSound = TreeMap<Double, SoundEvent>()
    val soundToFreq = mutableMapOf<SoundEvent, Double>()

    init {
        var count = 0
        for (event in SinWaves.waves) {
            register(event, SoundEvent.of("custom.sin_$count", null))
            count++
        }
    }

    fun register(freq: Double, soundEvent: SoundEvent) {
        freqToSound[ln(freq)] = soundEvent
        soundToFreq[soundEvent] = freq
    }

    fun getFrequency(sound: SoundEvent): Double =
        soundToFreq[sound] ?: error("SoundEvent $sound не зарегистрирован в SinewaveRegistry")

    fun getBestSound(freq2: Double): SoundEvent {
        val logFreq = ln(freq2)
        val lowKey = freqToSound.floorKey(logFreq)
        val highKey = freqToSound.ceilingKey(logFreq)

        return when {
            lowKey == null && highKey != null -> freqToSound[highKey]!!
            highKey == null && lowKey != null -> freqToSound[lowKey]!!
            lowKey != null -> {
                val mid = (lowKey + highKey) / 2
                if (mid > logFreq) freqToSound[lowKey]!! else freqToSound[highKey]!!
            }
            else -> freqToSound.values.first()
        }
    }
}