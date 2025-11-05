pluginManagement {
    val kotlinVersion = providers.gradleProperty("kotlinVersion").get()
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("dev.akif.tapik.plugin.gradle") version "0.1.0"
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

val kotlinVersionProperty = providers.gradleProperty("kotlinVersion").get()
val kotlinVersionInCatalog =
    Regex("""(?m)^\s*kotlin\s*=\s*"([^"]+)"""")
        .find(file("gradle/libs.versions.toml").readText())
        ?.groupValues
        ?.getOrNull(1)
check(
    kotlinVersionInCatalog == kotlinVersionProperty
) {
    "Expected kotlinVersion property ($kotlinVersionProperty) to match catalog definition ($kotlinVersionInCatalog)"
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":codec")
include(":core")
include(":gradle-plugin")
include(":jackson")
include(":common-plugin")
include(":spring-restclient")
include(":spring-webmvc")

rootProject.name = "tapik"
