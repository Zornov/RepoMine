package dev.zornov.repomine.common.registrar

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import org.slf4j.Logger
import su.plo.voice.api.addon.AddonInitializer
import su.plo.voice.api.server.PlasmoVoiceServer

@Singleton
class PlasmoVoiceAddonRegistrar(
    val voiceServer: PlasmoVoiceServer,
    val allAddons: List<AddonInitializer>,
    val logger: Logger,
) {
    @EventListener
    fun onContextStartup(event: StartupEvent) {
        allAddons.forEach { addon ->
            try {
                voiceServer.addonManager.load(addon)
                logger.info("Loaded addon: ${addon.javaClass.simpleName}")
            } catch (e: Exception) {
                logger.error("Failed to load addon ${addon.javaClass.simpleName}", e)
            }
        }
    }
}