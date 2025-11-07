import io.github.gradlenexus.publishplugin.NexusPublishExtension
import java.net.URI
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.plugins.signing.SigningExtension
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
    alias(libs.plugins.nexusPublish)
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

val syncDokkaHtmlToDocs by tasks.registering(Sync::class) {
    group = "documentation"
    description = "Copies aggregated Dokka HTML output under docs/api for MkDocs."
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
    from(layout.buildDirectory.dir("dokka/html"))
    into(layout.projectDirectory.dir("docs/api"))
}

tasks.named("dokkaGeneratePublicationHtml").configure {
    finalizedBy(syncDokkaHtmlToDocs)
}

tasks.matching { it.name == "dokkaGenerateHtml" }.configureEach {
    finalizedBy(syncDokkaHtmlToDocs)
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
    ":spring-restclient",
    ":spring-webmvc"
)
val signingKey = providers.gradleProperty("signingInMemoryKey").orNull
val signingKeyPassword = providers.gradleProperty("signingInMemoryKeyPassword").orNull
val mavenCentralUsername = providers.gradleProperty("mavenCentralUsername").orNull
val mavenCentralPassword = providers.gradleProperty("mavenCentralPassword").orNull
val shouldSignPublications = !signingKey.isNullOrBlank() && !signingKeyPassword.isNullOrBlank()

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
                            description.set("tapik ${project.name}")
                            url.set("https://github.com/makiftutuncu/tapik")
                            licenses {
                                license {
                                    name.set("MIT License")
                                    url.set("https://opensource.org/licenses/MIT")
                                    distribution.set("repo")
                                }
                            }
                            developers {
                                developer {
                                    id.set("makiftutuncu")
                                    name.set("Mehmet Akif Tütüncü")
                                    url.set("https://akif.dev")
                                }
                            }
                            scm {
                                url.set("https://github.com/makiftutuncu/tapik")
                                connection.set("scm:git:https://github.com/makiftutuncu/tapik.git")
                                developerConnection.set("scm:git:ssh://git@github.com/makiftutuncu/tapik.git")
                            }
                        }
                    }
                }
            }
            if (shouldSignPublications) {
                pluginManager.apply("signing")
                extensions.configure<SigningExtension>("signing") {
                    useInMemoryPgpKeys(signingKey, signingKeyPassword)
                    sign(extensions.getByType(PublishingExtension::class).publications)
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

extensions.configure<NexusPublishExtension>("nexusPublishing") {
    packageGroup.set("dev.akif.tapik")
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            mavenCentralUsername?.let { username.set(it) }
            mavenCentralPassword?.let { password.set(it) }
        }
    }
}
