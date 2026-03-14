package dev.akif.tapik.plugin

/**
 * Logging callbacks used by the shared Tapik code-generation engine.
 */
interface TapikLogger {
    /**
     * Logs an informational message.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    fun info(message: String, error: Throwable? = null)

    /**
     * Logs a debug message.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    fun debug(message: String, error: Throwable? = null)

    /**
     * Logs a warning message.
     *
     * @param message message to log.
     * @param error optional error associated with the message.
     */
    fun warn(message: String, error: Throwable? = null)

    /**
     * Logger implementation that prints messages to the standard output stream.
     */
    object Console : TapikLogger {
        override fun info(message: String, error: Throwable?) {
            log("[tapik] [INFO] $message", error)
        }

        override fun debug(message: String, error: Throwable?) {
            log("[tapik] [DEBUG] $message", error)
        }

        override fun warn(message: String, error: Throwable?) {
            log("[tapik] [WARN] $message", error)
        }

        private fun log(message: String, error: Throwable? = null) {
            println(message)
            error?.printStackTrace()
        }
    }

    /**
     * Logger implementation that discards every message.
     */
    object NoOp : TapikLogger {
        override fun info(message: String, error: Throwable?) {}
        override fun debug(message: String, error: Throwable?) {}
        override fun warn(message: String, error: Throwable?) {}
    }
}
