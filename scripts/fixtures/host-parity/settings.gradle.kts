import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        maven {
            url = uri(providers.gradleProperty("tapikRepository").get())
            content { includeGroupByRegex("dev[.]akif([.].*)?") }
        }
        gradlePluginPortal {
            content { excludeGroupByRegex("dev[.]akif([.].*)?") }
        }
        mavenCentral {
            content { excludeGroupByRegex("dev[.]akif([.].*)?") }
        }
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.4.10"
        id("dev.akif.tapik.plugin.gradle") version providers.gradleProperty("tapikVersion").get()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri(providers.gradleProperty("tapikRepository").get())
            content { includeGroupByRegex("dev[.]akif([.].*)?") }
        }
        mavenCentral {
            content { excludeGroup("dev.akif") }
        }
    }
}

rootProject.name = "tapik-host-parity"
