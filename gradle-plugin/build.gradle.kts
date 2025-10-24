plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.dokka)
}

repositories {
    mavenCentral()
}

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()

kotlin {
    jvmToolchain(javaVersion)
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":common-plugin"))
}

gradlePlugin {
    plugins {
        create("tapikGradlePlugin") {
            id = "dev.akif.tapik.plugin.gradle"
            implementationClass = "dev.akif.tapik.plugin.gradle.TapikGradlePlugin"
            displayName = "Tapik Gradle Plugin"
            description = "Tapik code generator Gradle plugin"
            tags.set(listOf("tapik", "codegen"))
        }
    }
}

publishing {
    publications { /* created automatically by java-gradle-plugin; keep for mavenLocal */ }
}
