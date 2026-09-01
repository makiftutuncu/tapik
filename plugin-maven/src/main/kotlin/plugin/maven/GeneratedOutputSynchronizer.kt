package dev.akif.tapik.plugin.maven

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.Base64
import java.util.stream.Collectors
import kotlin.io.path.isDirectory

internal object GeneratedOutputSynchronizer {
    @Synchronized
    fun synchronize(
        stagingDirectory: Path,
        outputDirectory: Path,
        stateDirectory: Path,
        owner: String
    ) {
        require(owner.isNotBlank()) { "Generated output owner must not be blank" }
        val staging = stagingDirectory.toAbsolutePath().normalize()
        val output = outputDirectory.toAbsolutePath().normalize()
        val stateFile = stateDirectory.toAbsolutePath().normalize().resolve(OWNERSHIP_FILE)
        val currentPaths = relativeFiles(staging)
        val ownership = readOwnership(stateFile).toMutableMap()
        val conflicts =
            ownership
                .filterKeys { candidate -> candidate != owner }
                .flatMap { (candidate, paths) -> paths.intersect(currentPaths).map { path -> path to candidate } }
        check(conflicts.isEmpty()) {
            conflicts.joinToString(
                prefix = "Generated output paths are already owned by other executions: ",
                transform = { (path, candidate) -> "'$path' by '$candidate'" }
            )
        }

        val stalePaths = ownership[owner].orEmpty() - currentPaths
        stalePaths.forEach { path -> Files.deleteIfExists(output.resolve(path)) }
        stalePaths.forEach { path -> deleteEmptyParents(output.resolve(path).parent, output) }
        currentPaths.forEach { path ->
            val destination = output.resolve(path)
            Files.createDirectories(requireNotNull(destination.parent))
            Files.copy(staging.resolve(path), destination, REPLACE_EXISTING)
        }

        if (currentPaths.isEmpty()) ownership.remove(owner) else ownership[owner] = currentPaths
        writeOwnership(stateFile, ownership)
    }

    private fun relativeFiles(root: Path): Set<String> {
        if (!Files.isDirectory(root)) return emptySet()
        return Files.walk(root).use { paths ->
            paths
                .filter(Files::isRegularFile)
                .map { path -> root.relativize(path).toString().replace(path.fileSystem.separator, "/") }
                .collect(Collectors.toCollection(::linkedSetOf))
        }
    }

    private fun readOwnership(stateFile: Path): Map<String, Set<String>> {
        if (!Files.isRegularFile(stateFile)) return emptyMap()
        val ownership = linkedMapOf<String, MutableSet<String>>()
        Files.readAllLines(stateFile, UTF_8).forEach { line ->
            val fields = line.split('\t')
            check(fields.size == 2) { "Invalid Tapik generated-output ownership at '$stateFile'" }
            ownership.getOrPut(fields[0].decoded(), ::linkedSetOf).add(fields[1].decoded())
        }
        return ownership
    }

    private fun writeOwnership(
        stateFile: Path,
        ownership: Map<String, Set<String>>
    ) {
        if (ownership.isEmpty()) {
            Files.deleteIfExists(stateFile)
            return
        }
        val lines =
            ownership.toSortedMap().flatMap { (candidate, paths) ->
                paths.sorted().map { path -> "${candidate.encoded()}\t${path.encoded()}" }
            }
        Files.createDirectories(requireNotNull(stateFile.parent))
        val temporary = Files.createTempFile(stateFile.parent, ".tapik-output-ownership-", ".tmp")
        try {
            Files.write(temporary, lines, UTF_8)
            moveReplacing(temporary, stateFile)
        } finally {
            Files.deleteIfExists(temporary)
        }
    }
}

private fun deleteEmptyParents(path: Path?, root: Path) {
    var current = path
    while (current != null && current != root && current.isDirectory() && isEmpty(current)) {
        Files.delete(current)
        current = current.parent
    }
}

private fun isEmpty(directory: Path): Boolean = Files.list(directory).use { entries -> entries.findAny().isEmpty }

private fun moveReplacing(source: Path, target: Path) {
    try {
        Files.move(source, target, REPLACE_EXISTING, ATOMIC_MOVE)
    } catch (_: AtomicMoveNotSupportedException) {
        Files.move(source, target, REPLACE_EXISTING)
    }
}

private const val OWNERSHIP_FILE: String = "generated-output-ownership"
private val ENCODER: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
private val DECODER: Base64.Decoder = Base64.getUrlDecoder()

private fun String.encoded(): String = ENCODER.encodeToString(toByteArray(UTF_8))

private fun String.decoded(): String = String(DECODER.decode(this), UTF_8)
