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
import org.gradle.api.GradleException
import org.gradle.work.DisableCachingByDefault
import java.io.File

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

    @get:Input
    abstract val rootPath: Property<String>

    @get:Input
    @get:Optional
    abstract val autoCommit: Property<Boolean>

    private fun bumpPatch(current: String): String {
        val parts = current.split(".").mapNotNull { it.toIntOrNull() }
        require(parts.size == 3) { "Current version must be in SemVer form (found $current)" }
        val (major, minor, patch) = parts
        return "$major.$minor.${patch + 1}"
    }

    private fun runGit(rootDir: File, vararg args: String) {
        val process = ProcessBuilder(listOf("git", *args))
            .directory(rootDir)
            .inheritIO()
            .start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw GradleException("git ${args.joinToString(" ")} failed with exit code $exitCode")
        }
    }

    @TaskAction
    fun updateVersions() {
        val rootDir = File(rootPath.get())
        val old = currentVersion.get()
        val explicitNew = newVersion.orNull?.trim().takeUnless { it.isNullOrBlank() }
        val next = explicitNew ?: bumpPatch(old)

        require(next.isNotEmpty()) { "newVersion must not be blank" }
        require(old != next) { "newVersion must differ from the current version" }

        val updatedFiles = mutableListOf<File>()

        targetFiles.get().forEach { file ->
            val target = file.asFile
            if (!target.exists()) {
                logger.lifecycle("Skipping missing ${target.relativeTo(rootDir)}")
                return@forEach
            }

            val original = target.readText(Charsets.UTF_8)
            val updated = original.replace(old, next)

            if (original != updated) {
                target.writeText(updated, Charsets.UTF_8)
                updatedFiles.add(target)
                logger.lifecycle(
                    "Updated version from $old to $next in ${target.relativeTo(rootDir)}"
                )
            }
        }

        if (updatedFiles.isEmpty()) {
            logger.lifecycle("No version references updated; skipping git commit.")
            return
        }

        if (autoCommit.orNull == false) {
            logger.lifecycle("Auto-commit disabled; skipping git commit.")
            return
        }

        runGit(rootDir, "rev-parse", "--is-inside-work-tree")
        runGit(rootDir, "add", "--", *updatedFiles.map { it.relativeTo(rootDir).path }.toTypedArray())
        runGit(rootDir, "commit", "-m", "Bump version from $old to $next")
    }
}
