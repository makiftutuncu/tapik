pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("dev.akif.tapik.gradle") version "0.1.0"
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app")
include(":codec")
include(":core")
include(":gradle-plugin")
include(":jackson")
include(":spring-restclient")

rootProject.name = "tapik"
