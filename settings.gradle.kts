dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app")
include(":codec")
include(":code-generator")
include(":core")
include(":jackson")
include(":spring-restclient")
include(":types")

rootProject.name = "tapik"
