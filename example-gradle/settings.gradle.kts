import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    val localTapikRepository = providers.gradleProperty("tapikRepository").orNull
    repositories {
        if (localTapikRepository != null) {
            maven {
                name = "localTapik"
                url = uri(localTapikRepository)
                content { includeGroupByRegex("dev[.]akif([.].*)?") }
            }
        }
        gradlePluginPortal {
            content { excludeGroupByRegex("dev[.]akif([.].*)?") }
        }
        mavenCentral {
            content { excludeGroupByRegex("dev[.]akif([.].*)?") }
        }
    }
    // tag::plugin-versions[]
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.4.10"
        id("dev.akif.tapik.plugin.gradle") version providers.gradleProperty("tapikVersion").getOrElse("0.6.0")
    }
    // end::plugin-versions[]
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    val localTapikRepository = providers.gradleProperty("tapikRepository").orNull
    repositories {
        if (localTapikRepository != null) {
            maven {
                name = "localTapik"
                url = uri(localTapikRepository)
                content { includeGroupByRegex("dev[.]akif([.].*)?") }
            }
        }
        mavenCentral {
            content { excludeGroup("dev.akif") }
        }
    }
}

rootProject.name = "tapik-example-gradle"
include("contract", "application")
project(":contract").projectDir = file("../example-contract")
project(":application").projectDir = file("../example-application")
