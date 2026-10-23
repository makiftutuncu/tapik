package dev.akif.tapik.plugin.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

internal fun Project.configureTapikEnvironment(generation: TaskProvider<TapikGenerateTask>) {
    generation.configure { task ->
        task.kotlinJvmProject.convention(false)
        task.projectType.convention("a project without org.jetbrains.kotlin.jvm")
        task.gradleVersion.convention(GradleVersion.current().version)
        task.javaVersion.convention(JavaVersion.current().majorVersion)
        task.kotlinVersion.convention("unavailable")
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        generation.configure { task ->
            task.kotlinJvmProject.set(true)
            task.projectType.set("Kotlin/JVM")
            task.kotlinVersion.set(getKotlinPluginVersion(logger))
        }
    }
    mapOf(
        "org.jetbrains.kotlin.multiplatform" to "Kotlin Multiplatform",
        "com.android.application" to "an Android application",
        "com.android.library" to "an Android library",
        "com.android.test" to "an Android test project",
        "com.android.dynamic-feature" to "an Android dynamic feature"
    ).forEach { (pluginId, description) ->
        pluginManager.withPlugin(pluginId) {
            generation.configure { task -> task.projectType.set(description) }
        }
    }
}

internal fun isSupportedGradleVersion(version: String): Boolean {
    val candidate = GradleVersion.version(version).baseVersion
    return candidate >= GradleVersion.version("9.1") && candidate < GradleVersion.version("9.9")
}
