pluginManagement {
    val kotlinVersion =
        providers.gradleProperty("kotlinVersion")
            .orElse(
                providers.provider {
                    val props = java.util.Properties()
                    file("../gradle.properties").inputStream().use { input ->
                        props.load(input)
                    }
                    props.getProperty("kotlinVersion")
                        ?: error("Gradle property 'kotlinVersion' is not defined.")
                }
            )
            .get()
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "buildSrc"
