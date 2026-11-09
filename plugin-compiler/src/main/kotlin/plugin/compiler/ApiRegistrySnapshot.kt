package dev.akif.tapik.plugin.compiler

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

internal class ApiRegistrySnapshot(
    private val snapshotFile: Path,
    sourceRoots: List<Path>
) {
    private val roots = sourceRoots.map(Path::normalized)

    fun read(): Map<Path, List<RegisteredApiType>> {
        if (!Files.isRegularFile(snapshotFile)) return emptyMap()
        val entries = linkedMapOf<Path, MutableList<RegisteredApiType>>()
        Files.readAllLines(snapshotFile, UTF_8).forEach { line ->
            val fields = line.split('\t')
            check(fields.size == 4) { "Invalid tapik API registry snapshot at '$snapshotFile'" }
            val rootIndex = fields[0].toInt()
            val root = roots.getOrNull(rootIndex)
            check(root != null) { "Invalid source root in tapik API registry snapshot at '$snapshotFile'" }
            val relativeSource = Path.of(fields[1].decodeIndexField())
            check(!relativeSource.isAbsolute) { "Invalid source path in tapik API registry snapshot at '$snapshotFile'" }
            val source = root.resolve(relativeSource).normalized()
            check(source.startsWith(root)) { "Invalid source path in tapik API registry snapshot at '$snapshotFile'" }
            val type = RegisteredApiType(fields[2].decodeIndexField(), ApiInstantiation.valueOf(fields[3]))
            entries.getOrPut(source, ::mutableListOf).add(type)
        }
        return entries
    }

    fun write(entries: Map<Path, List<RegisteredApiType>>) {
        if (entries.isEmpty()) {
            Files.deleteIfExists(snapshotFile)
            return
        }
        val lines =
            entries
                .toSortedMap(compareBy(Path::toString))
                .flatMap { (source, types) ->
                    val reference = sourceReference(source)
                    types.map { type ->
                        "${reference.rootIndex}\t${reference.relativePath.toString().encodeIndexField()}\t" +
                            "${type.internalName.encodeIndexField()}\t${type.instantiation.name}"
                    }
                }
        writeAtomically(snapshotFile, lines)
    }

    private fun sourceReference(source: Path): SourceReference {
        val normalizedSource = source.normalized()
        val match =
            roots
                .withIndex()
                .filter { (_, root) -> normalizedSource.startsWith(root) }
                .maxByOrNull { (_, root) -> root.nameCount }
        check(match != null) { "tapik source '$source' is outside its declared Kotlin source roots" }
        return SourceReference(match.index, match.value.relativize(normalizedSource))
    }
}

private data class SourceReference(
    val rootIndex: Int,
    val relativePath: Path
)
