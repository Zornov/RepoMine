package dev.zornov.repomine.factory

import dev.zornov.repomine.audio.plasmavoice.addon.VoiceListenerAddon
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.ShutdownEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import su.plo.voice.minestom.MinestomVoiceServer
import java.io.File

@Factory
class PlasmoVoiceServerFactory(
    val voiceAddons: List<VoiceListenerAddon>,
    val logger: Logger
) {

    lateinit var voiceServer: MinestomVoiceServer

    @Singleton
    fun minestomVoiceServer(): MinestomVoiceServer {
        logger.info("Initializing PlasmoVoice (Minestom) server…")
        val server = MinestomVoiceServer(File("config/voice/plasmavoice"))
        try {
            server.onInitialize()
        } catch (e: Exception) {
            logger.error("Failed to initialize PlasmoVoice server", e)
            throw e
        }

        voiceAddons.forEach { addon ->
            try {
                server.addonManager.load(addon)
            } catch (e: Exception) {
                logger.error("Failed to load addon ${addon.javaClass.simpleName}", e)
            }
        }

        voiceServer = server

        return server
    }

    @EventListener
    fun onShutdown(event: ShutdownEvent) {
        logger.info("Shutting down PlasmoVoice (Minestom) server…")
        try {
            voiceServer.onShutdown()
            logger.info("PlasmoVoice server stopped cleanly.")
        } catch (e: Exception) {
            logger.error("Error while shutting down PlasmoVoice server", e)
        }
    }

}
