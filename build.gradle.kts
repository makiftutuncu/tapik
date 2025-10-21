import java.net.URI
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()

plugins {
    alias(libs.plugins.dokka)
}

extensions.configure<DokkaExtension>("dokka") {
    pluginsConfiguration.named<DokkaHtmlPluginParameters>(
        DokkaHtmlPluginParameters.DOKKA_HTML_PARAMETERS_NAME
    ) {
        footerMessage.set("© 2025 Mehmet Akif Tütüncü")
    }
}

tasks.withType<DokkaGenerateTask>().configureEach {
    generator.failOnWarning.set(true)
}

tasks.withType<DokkaGeneratePublicationTask>().configureEach {
    outputDirectory.set(layout.buildDirectory.dir("dokka"))
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.dokka") {
        extensions.configure<DokkaExtension>("dokka") {
            pluginsConfiguration.named<DokkaHtmlPluginParameters>(
                DokkaHtmlPluginParameters.DOKKA_HTML_PARAMETERS_NAME
            ) {
                footerMessage.set("© 2025 Mehmet Akif Tütüncü")
            }
        }

        tasks.withType<DokkaGenerateTask>().configureEach {
            generator.failOnWarning.set(true)
        }

        tasks.withType<DokkaGenerateModuleTask>().configureEach {
            val modulePath =
                projectDir
                    .relativeTo(rootDir)
                    .invariantSeparatorsPath
                    .let { if (it.isBlank()) "" else "$it/" }

            generator.dokkaSourceSets.configureEach {
                jdkVersion.set(javaVersion)
                reportUndocumented.set(true)
                documentedVisibilities.set(
                    setOf(VisibilityModifier.Public, VisibilityModifier.Protected)
                )
                sourceLink {
                    val local = file("src/main/kotlin")
                    localDirectory.set(local)
                    if (local.exists()) {
                        remoteUrl.set(URI("https://github.com/makiftutuncu/tapik/blob/main/${modulePath}src/main/kotlin"))
                        remoteLineSuffix.set("#L")
                    }
                }
            }
        }
    }
}
