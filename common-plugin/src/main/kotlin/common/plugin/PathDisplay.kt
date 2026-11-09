package dev.akif.tapik.common.plugin

import java.nio.file.Path

/**
 * Displays this normalized absolute path relative to [projectDirectory] when both paths can be relativized.
 *
 * Paths from different filesystem providers fall back to the normalized absolute path. The returned display uses
 * forward slashes on every platform.
 */
fun Path.displayRelativeTo(projectDirectory: Path): String {
    val absoluteProject = projectDirectory.toAbsolutePath().normalize()
    val absolutePath = toAbsolutePath().normalize()
    val displayed =
        try {
            absoluteProject.relativize(absolutePath)
        } catch (_: IllegalArgumentException) {
            absolutePath
        }
    return displayed.toString().replace(displayed.fileSystem.separator, "/")
}
