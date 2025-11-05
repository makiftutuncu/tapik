import java.net.URI
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()
val projectVersion = version.toString()
val moduleTasks = mutableListOf<TaskProvider<DokkaGenerateModuleTask>>()

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint) apply false
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

val skipLintProperty = providers.gradleProperty("skipLint").map { true }.orElse(false)
val runPluginValidationProperty =
    providers.gradleProperty("runPluginValidation").map { true }.orElse(false)
val skipLintEnabled = skipLintProperty.get()
val runPluginValidationEnabled = runPluginValidationProperty.get()
val publishable = setOf(
    ":codec",
    ":common-plugin",
    ":core",
    ":jackson",
    ":spring-restclient"
)

val ktlintCheckTasks = mutableListOf<TaskProvider<Task>>()

subprojects {
    val skipLint = skipLintEnabled
    val runPluginValidation = runPluginValidationEnabled

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

    pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
        val ktlintCheck = tasks.named("ktlintCheck")
        ktlintCheckTasks += ktlintCheck

        ktlintCheck.configure {
            enabled = !skipLint
        }

        extensions.configure<KtlintExtension>("ktlint") {
            filter {
                include("**/*.kt")
                include("**/*.kts")
            }
            reporters {
                reporter(ReporterType.PLAIN)
            }
        }

        tasks.matching { it.name.contains("ktlint", ignoreCase = true) }.configureEach {
            enabled = !skipLint
        }
    }

    tasks.matching { it.name == "validatePlugins" }.configureEach {
        enabled = runPluginValidation
    }
}

gradle.projectsEvaluated {
    if (ktlintCheckTasks.isNotEmpty()) {
        val aggregate = tasks.register("ktlintCheckAll") {
            description = "Runs ktlint checks for all modules."
            group = "verification"
            dependsOn(ktlintCheckTasks)
            enabled = !skipLintEnabled
        }

        tasks.named("check").configure {
            dependsOn(aggregate)
        }
    }
}
