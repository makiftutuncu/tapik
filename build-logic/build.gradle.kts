import java.io.InputStream
import java.util.Properties
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

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
val buildLogicTargetVersion = minOf(javaTargetVersion, 24)

kotlin {
    jvmToolchain(javaToolchainVersion)
    compilerOptions {
        // Gradle's embedded Kotlin (kotlin-dsl) still tops out at JVM 24.
        jvmTarget.set(JvmTarget.fromTarget(buildLogicTargetVersion.toString()))
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
        freeCompilerArgs.add("-Xjdk-release=$buildLogicTargetVersion")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
    sourceCompatibility = JavaVersion.toVersion(buildLogicTargetVersion)
    targetCompatibility = JavaVersion.toVersion(buildLogicTargetVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(buildLogicTargetVersion)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
}
