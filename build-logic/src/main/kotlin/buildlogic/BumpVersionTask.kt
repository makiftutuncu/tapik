package buildlogic

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Updates repository files with the new project version.")
abstract class BumpVersionTask : DefaultTask() {
    @get:Input
    abstract val currentVersion: Property<String>

    @get:Input
    @get:Optional
    abstract val newVersion: Property<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val targetFiles: ListProperty<RegularFile>

    private fun bumpPatch(current: String): String {
        val parts = current.split(".").mapNotNull { it.toIntOrNull() }
        require(parts.size == 3) { "Current version must be in SemVer form (found $current)" }
        val (major, minor, patch) = parts
        return "$major.$minor.${patch + 1}"
    }

    @TaskAction
    fun updateVersions() {
        val rootDir = project.layout.projectDirectory.asFile
        val old = currentVersion.get()
        val explicitNew = newVersion.orNull?.trim().takeUnless { it.isNullOrBlank() }
        val next = explicitNew ?: bumpPatch(old)

        require(next.isNotEmpty()) { "newVersion must not be blank" }
        require(old != next) { "newVersion must differ from the current version" }

        targetFiles.get().forEach { file ->
            val target = file.asFile
            if (!target.exists()) {
                logger.lifecycle("Skipping missing ${target.relativeTo(rootDir)}")
                return@forEach
            }

            val original = target.readText()
            val updated = original.replace(old, next)

            if (original != updated) {
                target.writeText(updated)
                logger.lifecycle(
                    "Updated version from $old to $next in ${target.relativeTo(rootDir)}"
                )
            }
        }
    }
}
