package dev.akif.tapik.plugin.gradle

import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

internal fun TapikExtension.directoryForMode(
    selectedMode: Mode,
    generation: TaskProvider<TapikGenerateTask>,
    unusedRoot: Provider<Directory>,
    directory: String
): Provider<Directory> =
    mode.map { selected -> selected == selectedMode }.flatMap { selected ->
        if (selected) {
            generation.flatMap { task -> task.outputRoot.dir(directory) }
        } else {
            unusedRoot.map { root -> root.dir(directory) }
        }
    }.orElse(unusedRoot.map { root -> root.dir(directory) })

internal fun TapikExtension.outputDirectoryForMode(
    selectedMode: Mode,
    unusedRoot: Provider<Directory>,
    directory: String
): Provider<Directory> =
    mode.flatMap { selected ->
        if (selected == selectedMode) outputRoot.dir(directory)
        else unusedRoot.map { root -> root.dir(directory) }
    }.orElse(unusedRoot.map { root -> root.dir(directory) })

internal fun TapikExtension.classpathForMode(
    selectedMode: Mode,
    selectedClasspath: FileCollection,
    emptyClasspath: FileCollection
): Provider<FileCollection> =
    mode.map { selected ->
        if (selected == selectedMode) selectedClasspath else emptyClasspath
    }.orElse(emptyClasspath)
