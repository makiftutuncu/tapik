import java.net.URI
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()
val projectVersion = version.toString()
val moduleTasks = mutableListOf<TaskProvider<DokkaGenerateModuleTask>>()

plugins {
    alias(libs.plugins.dokka)
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:all-modules-page-plugin:${libs.versions.dokka.get()}")
}

extensions.configure<DokkaExtension>("dokka") {
    moduleName.set(rootProject.name)
    moduleVersion.set(projectVersion)
    pluginsConfiguration.named<DokkaHtmlPluginParameters>(
        DokkaHtmlPluginParameters.DOKKA_HTML_PARAMETERS_NAME
    ) {
        footerMessage.set("© 2025 Mehmet Akif Tütüncü")
    }
    dokkaPublications.named("html") {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
        failOnWarning.set(true)
        suppressObviousFunctions.set(true)
        suppressInheritedMembers.set(true)
    }
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}

gradle.projectsEvaluated {
    tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml").configure {
        dependsOn(moduleTasks)
        generator.moduleOutputDirectories.setFrom(moduleTasks.map { it.get().outputDirectory })
    }
}

val publishable = setOf(
    ":codec",
    ":common-plugin",
    ":core",
    ":jackson",
    ":spring-restclient"
)

subprojects {
    pluginManager.withPlugin("org.jetbrains.dokka") {
        val moduleTask = tasks.named<DokkaGenerateModuleTask>("dokkaGenerateModuleHtml")
        moduleTasks += moduleTask

        extensions.configure<DokkaExtension>("dokka") {
            moduleName.set(project.name)
            moduleVersion.set(projectVersion)

            val moduleRelativePath =
                projectDir
                    .relativeTo(rootDir)
                    .invariantSeparatorsPath

            if (moduleRelativePath.isNotBlank()) {
                modulePath.set(moduleRelativePath)
            }

            pluginsConfiguration.named<DokkaHtmlPluginParameters>(
                DokkaHtmlPluginParameters.DOKKA_HTML_PARAMETERS_NAME
            ) {
                footerMessage.set("© 2025 Mehmet Akif Tütüncü")
            }

            dokkaSourceSets.configureEach {
                jdkVersion.set(javaVersion)
                reportUndocumented.set(true)
                documentedVisibilities.set(
                    setOf(VisibilityModifier.Public, VisibilityModifier.Protected)
                )

                val local = project.file("src/main/kotlin")
                sourceLink {
                    localDirectory.set(local)
                    if (local.exists()) {
                        val remotePath =
                            if (moduleRelativePath.isBlank()) {
                                "src/main/kotlin"
                            } else {
                                "$moduleRelativePath/src/main/kotlin"
                            }
                        remoteUrl.set(URI("https://github.com/makiftutuncu/tapik/blob/main/$remotePath"))
                        remoteLineSuffix.set("#L")
                    }
                }
            }

            dokkaPublications.named("html") {
                outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
                failOnWarning.set(true)
                suppressObviousFunctions.set(true)
                suppressInheritedMembers.set(true)
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        if (path in publishable) {
            pluginManager.apply("java-library")
            pluginManager.apply("maven-publish")
            extensions.configure<JavaPluginExtension> {
                withSourcesJar()
                withJavadocJar()
            }
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])
                        artifactId = project.name
                        pom {
                            name.set(project.name)
                            url.set("https://github.com/makiftutuncu/tapik")
                        }
                    }
                }
            }
        }
    }
}
