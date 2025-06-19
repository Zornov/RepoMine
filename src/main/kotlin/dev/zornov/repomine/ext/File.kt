package dev.zornov.repomine.ext

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun Path.copyDirectory(target: Path) {
    Files.walk(this).use { paths ->
        paths.forEach { src ->
            val dest = target.resolve(this.relativize(src))
            if (Files.isDirectory(src)) {
                Files.createDirectories(dest)
            } else {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}
