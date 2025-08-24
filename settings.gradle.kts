dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":app")
include(":codec")
include(":core")
include(":jackson")
include(":spring-restclient")
include(":tuple")

rootProject.name = "tapik"
