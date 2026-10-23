import org.gradle.api.tasks.WriteProperties
import org.gradle.plugin.compatibility.compatibility
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka")
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish")
}

val tapikVersion = extra["tapikVersion"] as String
val tapikRepository = extra["tapikRepository"] as File
val rootPom = rootProject.file("../pom.xml").readText()
val kotlinVersion =
    requireNotNull(Regex("""<kotlin\.version>([^<]+)</kotlin\.version>""").find(rootPom)?.groupValues?.get(1)) {
        "Cannot read kotlin.version from the root Maven POM"
    }
val testKitPlugins =
    configurations.create("testKitPlugins") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
val generationWorkerContract =
    configurations.create("generationWorkerContract") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
val generationWorkerTargets =
    configurations.create("generationWorkerTargets") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }

dependencies {
    implementation("dev.akif:tapik-common-plugin:$tapikVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    testImplementation(platform("io.kotest:kotest-bom:6.2.4"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm")
    testImplementation("io.kotest:kotest-assertions-core-jvm")
    testImplementation(gradleTestKit())
    testKitPlugins("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    generationWorkerContract("dev.akif:tapik-example-contract:$tapikVersion")
    generationWorkerTargets("dev.akif:tapik-target-openapi:$tapikVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters.set(true)
    }
}

gradlePlugin {
    website.set("https://tapik.akif.dev")
    vcsUrl.set("https://github.com/makiftutuncu/tapik")

    plugins {
        create("tapik") {
            id = "dev.akif.tapik.plugin.gradle"
            implementationClass = "dev.akif.tapik.plugin.gradle.TapikGradlePlugin"
            displayName = "tapik Gradle plugin"
            description = "Generates artifacts from type-safe tapik API contracts"
            tags.set(listOf("tapik", "code-generation", "kotlin"))
            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}

tasks.named<Jar>("javadocJar") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
    from(layout.buildDirectory.dir("dokka/html"))
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("tapik Gradle plugin")
            description.set("Generates artifacts from type-safe tapik API contracts")
            url.set("https://tapik.akif.dev")
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
                connection.set("scm:git:https://github.com/makiftutuncu/tapik.git")
                developerConnection.set("scm:git:ssh://git@github.com/makiftutuncu/tapik.git")
                url.set("https://github.com/makiftutuncu/tapik")
                tag.set("HEAD")
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/makiftutuncu/tapik/issues")
            }
        }
    }

    val verificationRepository = providers.gradleProperty("tapikPublishRepository")
    if (verificationRepository.isPresent) {
        repositories {
            maven {
                name = "tapikVerification"
                url = uri(verificationRepository.get())
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("tapik.repository", tapikRepository)
    systemProperty("tapik.version", tapikVersion)
    systemProperty("tapik.generation-worker.contract-classpath", generationWorkerContract.asPath)
    systemProperty("tapik.generation-worker.target-classpath", generationWorkerTargets.asPath)
}

tasks.pluginUnderTestMetadata {
    pluginClasspath.from(testKitPlugins)
}

val generatedPluginResources = layout.buildDirectory.dir("generated/plugin-resources")
val generatePluginProperties = tasks.register<WriteProperties>("generatePluginProperties") {
    destinationFile =
        generatedPluginResources
            .get()
            .file("dev/akif/tapik/plugin/gradle/plugin.properties")
            .asFile
    property("version", tapikVersion)
}

sourceSets.main {
    resources.srcDir(generatedPluginResources)
}

tasks.processResources {
    dependsOn(generatePluginProperties)
}

tasks.named("sourcesJar") {
    dependsOn(generatePluginProperties)
}
