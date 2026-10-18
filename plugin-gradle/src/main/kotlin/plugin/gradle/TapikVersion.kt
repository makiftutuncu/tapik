package dev.akif.tapik.plugin.gradle

import java.util.Properties

internal val tapikVersion: String by lazy {
    val resource = "dev/akif/tapik/plugin/gradle/plugin.properties"
    val properties = Properties()
    val stream = requireNotNull(TapikGradlePlugin::class.java.classLoader.getResourceAsStream(resource)) {
        "Missing tapik Gradle plugin version resource '$resource'"
    }
    stream.use(properties::load)
    requireNotNull(properties.getProperty("version")) {
        "Missing tapik Gradle plugin version"
    }
}
