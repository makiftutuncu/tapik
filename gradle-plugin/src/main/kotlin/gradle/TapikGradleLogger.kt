package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.plugin.TapikLogger
import org.gradle.api.logging.Logger

/**
 * Gradle-backed [TapikLogger] implementation used by the Tapik Gradle plugin.
 *
 * @param logger Gradle logger receiving Tapik log events.
 */
class TapikGradleLogger(private val logger: Logger): TapikLogger {
    /**
     * Logs an informational message through Gradle lifecycle logging.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    override fun info(message: String, error: Throwable?) {
        logger.lifecycle("[tapik] [INFO] $message", error)
    }

    /**
     * Logs a debug message through Gradle debug logging.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    override fun debug(message: String, error: Throwable?) {
        logger.debug("[tapik] [DEBUG] $message", error)
    }

    /**
     * Logs a warning message through Gradle warning logging.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    override fun warn(message: String, error: Throwable?) {
        logger.warn("[tapik] [WARN] $message", error)
    }
}
