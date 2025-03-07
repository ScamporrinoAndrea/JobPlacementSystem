package com.example.doc_store

import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

@Component
class LoggerConfig {

    private val logger: Logger = Logger.getLogger(LoggerConfig::class.java.name)

    init {
        setupLogger()
    }

    private fun setupLogger() {
        try {
            // Percorso relativo per il file di log
            val logDir = Paths.get("logs")
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir) // Crea la directory se non esiste
            }

            val logPath = logDir.resolve("docStore.log").toString()

            // Configura il file handler per scrivere i log
            val fileHandler = FileHandler(logPath, true)
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)

            logger.info("Logger configurato per scrivere su $logPath")
        } catch (e: IOException) {
            logger.severe("Errore nella configurazione del logger: ${e.message}")
        }
    }

    fun info(message: String) {
        logger.info(message)
    }

    fun logError(message: String) {
        logger.severe(message)
    }
}
