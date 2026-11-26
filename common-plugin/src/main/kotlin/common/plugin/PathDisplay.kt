package dev.akif.tapik.common.plugin

import java.nio.file.Path

/**
 * Displays this path relative to [projectDirectory] when both paths can be relativized.
 *
 * Paths from different filesystem providers fall back to the normalized absolute path. The returned display uses
 * forward slashes on every platform.
 *
 * @param projectDirectory base directory for a relative display.
 * @return a project-relative path or normalized absolute path using forward slashes.
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

/**
 * Reports [paths] in iteration order using [Path.displayRelativeTo], isolating each reporting failure.
 *
 * This is intended only for post-commit diagnostics. An exception while displaying or reporting one path neither
 * escapes nor prevents later paths from being reported.
 *
 * @param paths generated paths to report in iteration order.
 * @param projectDirectory base directory for relative path displays.
 * @param report receives each formatted path.
 */
fun reportGeneratedPaths(
    paths: Iterable<Path>,
    projectDirectory: Path,
    report: (String) -> Unit
) {
    paths.forEach { path ->
        try {
            report(path.displayRelativeTo(projectDirectory))
        } catch (_: Exception) {
            // Generation is already committed; reporting must not change its outcome.
        }
    }
}
