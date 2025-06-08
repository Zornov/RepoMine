package dev.zornov.repomine.resourcepack

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


@Singleton
class ResourcePackGenerator(val logger: Logger) : ApplicationEventListener<StartupEvent> {
    override fun onApplicationEvent(event: StartupEvent) {
        val basePath = Path.of("src/main/resources")

        val config = PackBuilder.generate(
            basePath.resolve("bbmodel"),
            basePath.resolve("resourcepack"),
            Path.of("./tmp/models")
        )
        logger.info("Generated pack with model mappings")

        val mappingsFile = Path.of("./tmp/models/model_mappings.json")
        Files.writeString(mappingsFile, config.modelMappings(), StandardCharsets.UTF_8)
        logger.info("Wrote model mappings to ${mappingsFile.fileName}")

        logger.info("Resource pack generation complete.")

        ModelEngine.setModelMaterial(Material.MAGMA_CREAM)
        ModelEngine.loadMappings(Files.newBufferedReader(mappingsFile), Path.of("./tmp/models"))
    }

}
