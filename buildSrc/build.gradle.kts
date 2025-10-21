import java.io.InputStream
import java.util.Properties

plugins {
    `kotlin-dsl`
}

val javaVersion =
    providers.gradleProperty("javaVersion").map(String::toInt).orElse(
        providers.provider {
            val props = Properties()
            file("../gradle.properties").inputStream().use { input: InputStream ->
                props.load(input)
            }
            props.getProperty("javaVersion")?.toInt()
                ?: error("Gradle property 'javaVersion' is not defined.")
        }
    ).get()

kotlin {
    jvmToolchain(javaVersion)
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
}
