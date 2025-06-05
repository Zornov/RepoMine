package dev.zornov.repomine.audio.vanilla.api

import dev.zornov.repomine.audio.vanilla.audio.AudioFrame

interface AudioSource {
    fun prepareNext(): Boolean
    fun getCurrent(): AudioFrame
}