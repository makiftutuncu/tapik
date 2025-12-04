import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import java.net.URI
import java.time.LocalDate
import org.gradle.api.Task
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.engine.plugins.DokkaHtmlPluginParameters
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateModuleTask
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.gradle.plugins.signing.Sign

val javaTargetVersion = providers.gradleProperty("javaTargetVersion").map(String::toInt).get()
val projectVersion = version.toString()
val moduleTasks = mutableListOf<TaskProvider<DokkaGenerateModuleTask>>()

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.kotlinJvm) apply false
    id("buildlogic.bump-version")
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
        footerMessage.set("&#169; ${LocalDate.now().year} Mehmet Akif Tütüncü")
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

val dokkaGenerateTask = tasks.named("dokkaGenerate")

val syncDokkaHtmlToDocs by tasks.registering(Sync::class) {
    group = "documentation"
    description = "Copies aggregated Dokka HTML output under docs/api for MkDocs."
    dependsOn(dokkaGenerateTask)
    from(layout.buildDirectory.dir("dokka/html"))
    into(layout.projectDirectory.dir("docs/api"))
}

dokkaGenerateTask.configure {
    finalizedBy(syncDokkaHtmlToDocs)
}
val skipLintProperty = providers.gradleProperty("skipLint").map { true }.orElse(false)
val runPluginValidationProperty =
    providers.gradleProperty("runPluginValidation").map { true }.orElse(false)
val skipLintEnabled = skipLintProperty.get()
val runPluginValidationEnabled = runPluginValidationProperty.get()
val kotlinPublishable = setOf(
    ":codec",
    ":common-plugin",
    ":core",
    ":jackson",
    ":spring-restclient",
    ":spring-webmvc"
)
val gradlePluginPublishable = setOf(":gradle-plugin")

val ktlintCheckTasks = mutableListOf<TaskProvider<Task>>()

subprojects {
    val skipLint = skipLintEnabled
    val runPluginValidation = runPluginValidationEnabled
    var dokkaHtmlOutputsTask: TaskProvider<Task>? = null

    pluginManager.withPlugin("org.jetbrains.dokka") {
        val moduleTask = tasks.named<DokkaGenerateModuleTask>("dokkaGenerateModuleHtml")
        moduleTasks += moduleTask

        val dokkaGenerateLifecycle = tasks.named("dokkaGenerate")
        val dokkaHtmlOutputDir = layout.buildDirectory.dir("dokka/html")
        val tapikDokkaHtmlOutputs =
            tasks.register("tapikDokkaHtmlOutputs") {
                group = "documentation"
                description = "Exposes Dokka HTML output for Jar packaging."
                dependsOn(dokkaGenerateLifecycle)
                outputs.dir(dokkaHtmlOutputDir)
            }
        dokkaHtmlOutputsTask = tapikDokkaHtmlOutputs

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
                footerMessage.set("&#169; ${LocalDate.now().year} Mehmet Akif Tütüncü")
            }

            dokkaSourceSets.configureEach {
                jdkVersion.set(javaTargetVersion)
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

            tasks.matching { it.name == "generateMetadataFileForMavenPublication" }.configureEach {
                dependsOn("dokkaJavadocJar")
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        if (path in kotlinPublishable) {
            pluginManager.apply("java-library")
            pluginManager.apply("maven-publish")
            extensions.configure<JavaPluginExtension> {
                withSourcesJar()
            }
            pluginManager.apply("com.vanniktech.maven.publish")
            pluginManager.withPlugin("org.jetbrains.dokka") {
                extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
                    configure(
                        KotlinJvm(
                            javadocJar = JavadocJar.Dokka(
                                dokkaHtmlOutputsTask ?: tasks.named("tapikDokkaHtmlOutputs")
                            ),
                            sourcesJar = true
                        )
                    )
                }
            }
        }
    }

    plugins.withId("java-gradle-plugin") {
        if (path in gradlePluginPublishable) {
            extensions.configure<JavaPluginExtension> {
                withSourcesJar()
            }
            pluginManager.apply("com.vanniktech.maven.publish")
            pluginManager.withPlugin("org.jetbrains.dokka") {
                extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
                    configure(
                        GradlePlugin(
                            javadocJar = JavadocJar.Dokka(
                                dokkaHtmlOutputsTask ?: tasks.named("tapikDokkaHtmlOutputs")
                            ),
                            sourcesJar = true
                        )
                    )
                }
            }
        }
    }

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.withType<Sign>())
}

val publishingToMavenLocal =
    gradle.startParameter.taskNames.any { it.contains("ToMavenLocal") }

tasks.withType<Sign>().configureEach {
    onlyIf("Signing is skipped for mavenLocal publications") { !publishingToMavenLocal }
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
