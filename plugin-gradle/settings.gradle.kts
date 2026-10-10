import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("dev[.]akif([.].*)?")
            }
        }
        mavenCentral {
            content {
                excludeGroupByRegex("dev[.]akif([.].*)?")
            }
        }
    }

    val rootPom = file("../pom.xml").readText()
    val kotlinVersion =
        requireNotNull(Regex("""<kotlin\.version>([^<]+)</kotlin\.version>""").find(rootPom)?.groupValues?.get(1)) {
            "Cannot read kotlin.version from the root Maven POM"
        }
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

rootProject.name = "tapik-plugin-gradle"

val rootPom = file("../pom.xml").readText()
val tapikVersion =
    requireNotNull(
        Regex("""<artifactId>tapik-parent</artifactId>\s*<version>([^<]+)</version>""")
            .find(rootPom)
            ?.groupValues
            ?.get(1)
    ) {
        "Cannot read the tapik version from the root Maven POM"
    }
val suppliedTapikVersion = providers.gradleProperty("tapikVersion").orNull
check(suppliedTapikVersion == null || suppliedTapikVersion == tapikVersion) {
    "tapik version mismatch: the Gradle build received $suppliedTapikVersion but the root Maven POM declares $tapikVersion"
}

val tapikRepository =
    providers
        .gradleProperty("tapikRepository")
        .map(::file)
        .orElse(file("../target/gradle-plugin-repository"))
        .get()
        .absoluteFile
        .normalize()
check(tapikRepository.isDirectory) {
    "Missing isolated tapik Maven repository at '$tapikRepository'. " +
        "Prepare it from the repository root or pass -PtapikRepository=<directory>."
}

gradle.beforeProject {
    group = "dev.akif"
    version = tapikVersion
    extensions.extraProperties["tapikVersion"] = tapikVersion
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            name = "tapikBuild"
            url = tapikRepository.toURI()
            content {
                includeGroup("dev.akif")
            }
        }
        mavenCentral {
            content {
                excludeGroup("dev.akif")
            }
        }
    }
}
