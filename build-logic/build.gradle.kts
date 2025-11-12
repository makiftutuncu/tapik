import java.io.InputStream
import java.util.Properties
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

fun intProperty(name: String) =
    providers.gradleProperty(name).map(String::toInt).orElse(
        providers.provider {
            val props = Properties()
            file("../gradle.properties").inputStream().use { input: InputStream ->
                props.load(input)
            }
            props.getProperty(name)?.toInt()
                ?: error("Gradle property '$name' is not defined.")
        }
    )

val javaTargetVersion = intProperty("javaTargetVersion").get()
val javaToolchainVersion = intProperty("javaToolchainVersion").get()

kotlin {
    jvmToolchain(javaToolchainVersion)
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(javaTargetVersion.toString()))
        freeCompilerArgs.add("-Xjdk-release=$javaTargetVersion")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
    sourceCompatibility = JavaVersion.toVersion(javaTargetVersion)
    targetCompatibility = JavaVersion.toVersion(javaTargetVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaTargetVersion)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
}
