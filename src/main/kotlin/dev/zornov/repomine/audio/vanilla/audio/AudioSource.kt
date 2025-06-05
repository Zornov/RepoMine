package dev.zornov.repomine.audio.vanilla.audio

interface AudioSource {
    fun prepareNext(): Boolean
    fun getCurrent(): AudioFrame
}