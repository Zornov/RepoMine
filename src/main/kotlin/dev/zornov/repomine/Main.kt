package dev.zornov.repomine

import io.micronaut.runtime.Micronaut
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

object Main {
    val logger = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            measureTimeMillis {
                Micronaut.build()
                    .banner(true)
                    .mainClass(Main::class.java)
                    .args(*args)
                    .start()
            }.let {
                logger.info("Server started in %.2f sec.".format(it / 1000.0))
            }
        } catch (ex: Exception) {
            logger.error("Server startup error", ex)
            exitProcess(1)
        }
    }
}
