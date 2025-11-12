package buildlogic.convention

import java.io.File
import java.io.InputStream
import java.util.Properties
import kotlin.collections.buildList
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.configureKotlinAndJavaToolchains() {
    val javaTargetVersion = intProperty("javaTargetVersion")
    val javaToolchainVersion = intProperty("javaToolchainVersion")

    extensions.findByType(KotlinJvmProjectExtension::class.java)?.apply {
        jvmToolchain(javaToolchainVersion)
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaTargetVersion.toString()))
            freeCompilerArgs.add("-Xjdk-release=$javaTargetVersion")
        }
    }

    extensions.findByType(JavaPluginExtension::class.java)?.apply {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
        sourceCompatibility = JavaVersion.toVersion(javaTargetVersion)
        targetCompatibility = JavaVersion.toVersion(javaTargetVersion)
    }

    tasks.withType(JavaCompile::class.java).configureEach {
        options.release.set(javaTargetVersion)
    }
}

fun Project.intProperty(name: String): Int =
    providers.gradleProperty(name).map(String::toInt).orElse(
        providers.provider {
            val props = Properties()
            val candidate = findGradlePropertiesFile()
                ?: error("Gradle property '$name' is not defined.")
            candidate.inputStream().use { input: InputStream ->
                props.load(input)
            }
            props.getProperty(name)?.toInt()
                ?: error("Gradle property '$name' is not defined.")
        }
    ).get()

private fun Project.findGradlePropertiesFile(): File? {
    val locations = buildList<File> {
        add(rootProject.layout.projectDirectory.file("gradle.properties").asFile)
        add(projectDir.resolve("gradle.properties"))
        projectDir.parentFile?.resolve("gradle.properties")?.let { add(it) }
        add(File(rootDir, "../gradle.properties").normalize())
    }
    return locations.firstOrNull { it.exists() }
}
