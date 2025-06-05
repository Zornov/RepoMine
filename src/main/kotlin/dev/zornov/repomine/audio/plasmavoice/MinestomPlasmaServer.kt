package dev.zornov.repomine.audio.plasmavoice

import dev.zornov.repomine.audio.plasmavoice.addon.VoiceListenerAddon
import io.micronaut.context.event.ShutdownEvent
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import su.plo.voice.minestom.MinestomVoiceServer
import java.io.File

@Suppress("unused")
@Singleton
class MinestomPlasmaServer(
    val logger: Logger
) {
    lateinit var server: MinestomVoiceServer

    @EventListener
    fun onStartup(event: StartupEvent) {
        logger.info("Starting PlasmaVoice (Minestom) server…")
        try {
            server = MinestomVoiceServer(File("config/voice/plasmavoice"))
            server.onInitialize()
            logger.info("PlasmaVoice server successfully initialized.")
        } catch (e: Exception) {
            logger.error("Failed to initialize PlasmaVoice server", e)
        }

        server.addonManager.load(VoiceListenerAddon())
    }

    @EventListener
    fun onShutdown(event: ShutdownEvent) {
        logger.info("Shutting down PlasmaVoice (Minestom) server…")
        try {
            server.onShutdown()
            logger.info("PlasmaVoice server stopped cleanly.")
        } catch (e: Exception) {
            logger.error("Error while shutting down PlasmaVoice server", e)
        }
    }
}