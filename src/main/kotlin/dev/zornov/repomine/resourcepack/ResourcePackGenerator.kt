package dev.zornov.repomine.resourcepack

import dev.zornov.repomine.ext.copyDirectory
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import jakarta.inject.Singleton
import net.minestom.server.item.Material
import net.worldseed.multipart.ModelEngine
import net.worldseed.resourcepack.PackBuilder
import org.slf4j.Logger
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists

@Singleton
class ResourcePackGenerator(
    val logger: Logger,
    val server: ResourcePackService
) : ApplicationEventListener<StartupEvent> {

    override fun onApplicationEvent(event: StartupEvent) {
        val base = Path.of("src/main/resources")
        val tmp = Path.of("./tmp")
        val resourcepackTmp = tmp.resolve("resourcepack")
        val modelsTmp = tmp.resolve("models")
        val mappingsFile = modelsTmp.resolve("model_mappings.json")

        listOf(resourcepackTmp, modelsTmp).forEach { if (it.exists()) it.toFile().deleteRecursively() }
        if (mappingsFile.exists()) mappingsFile.deleteExisting()

        val config = PackBuilder.generate(
            base.resolve("blockbench"),
            resourcepackTmp,
            modelsTmp
        )
        Files.createDirectories(modelsTmp)
        Files.writeString(mappingsFile, config.modelMappings(), StandardCharsets.UTF_8)

        base.resolve("resourcepack").copyDirectory(resourcepackTmp)

        ModelEngine.setModelMaterial(Material.MAGMA_CREAM)
        Files.newBufferedReader(mappingsFile).use { reader ->
            ModelEngine.loadMappings(reader, modelsTmp)
        }

        logger.info("Resource pack generation complete.")

        server.onServerStart()
    }
}
