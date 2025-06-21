package dev.zornov.repomine.resourcepack

import com.sun.net.httpserver.HttpServer
import jakarta.inject.Singleton
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.	adventure.resource.ResourcePackRequest
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Singleton
class ResourcePackService {
    val tmpFolder = Paths.get("./tmp")
    val resourceFolder: Path = tmpFolder.resolve("resourcepack")
    val zipOutput: Path = tmpFolder.resolve("resourcepack.zip")
    val port: Int = 5001

    lateinit var request: ResourcePackRequest

    fun onServerStart() {
        try {
            zipResourcePack()
            startHttpServer()
            createResourcePackInfo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun zipResourcePack() {
        if (Files.exists(zipOutput)) {
            Files.delete(zipOutput)
        }

        ZipOutputStream(Files.newOutputStream(zipOutput)).use { zos ->
            Files.walkFileTree(resourceFolder, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val entry = ZipEntry(resourceFolder.relativize(file).toString().replace("\\", "/"))
                    zos.putNextEntry(entry)
                    Files.copy(file, zos)
                    zos.closeEntry()
                    return FileVisitResult.CONTINUE
                }
            })
        }
    }

    fun startHttpServer() {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/resourcepack.zip") { exchange ->
            try {
                val data = Files.readAllBytes(zipOutput)
                exchange.responseHeaders.add("Content-Type", "application/zip")
                exchange.sendResponseHeaders(200, data.size.toLong())
                exchange.responseBody.use { it.write(data) }
            } catch (e: IOException) {
                e.printStackTrace()
                exchange.sendResponseHeaders(500, 0)
            } finally {
                exchange.close()
            }
        }

        server.executor = Executors.newSingleThreadExecutor()
        server.start()
    }

    fun createResourcePackInfo() {
        val resourcePackInfoFuture = ResourcePackInfo.resourcePackInfo()
            .id(UUID.randomUUID())
            .uri(URI.create("http://localhost:$port/resourcepack.zip"))
            .computeHashAndBuild()

        resourcePackInfoFuture.thenAccept { info ->
            request = ResourcePackRequest.resourcePackRequest()
                .packs(info)
                .required(true)
                .build()
        }.exceptionally {
            it.printStackTrace()
            null
        }
    }
}
