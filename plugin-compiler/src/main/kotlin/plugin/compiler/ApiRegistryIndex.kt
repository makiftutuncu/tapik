package dev.akif.tapik.plugin.compiler

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.Base64

internal class ApiRegistryIndex(
    private val stateFile: Path
) {
    fun synchronize(
        incremental: Boolean,
        activeSources: Set<Path>,
        compiledSources: Map<Path, List<RegisteredApiType>>
    ): List<RegisteredApiType> {
        val active = activeSources.mapTo(linkedSetOf(), Path::normalized)
        val compiled = compiledSources.mapKeys { (source, _) -> source.normalized() }
        val entries =
            if (incremental) {
                read()
                    .filterKeys(active::contains)
                    .toMutableMap()
                    .apply {
                        compiled.forEach { (source, apiTypes) ->
                            if (apiTypes.isEmpty()) remove(source) else put(source, apiTypes)
                        }
                    }
            } else {
                compiled.filterValues(List<RegisteredApiType>::isNotEmpty).toMutableMap()
            }
        write(entries)
        return entries
            .toSortedMap(compareBy(Path::toString))
            .values
            .flatten()
    }

    private fun read(): Map<Path, List<RegisteredApiType>> {
        if (!Files.isRegularFile(stateFile)) return emptyMap()
        val entries = linkedMapOf<Path, MutableList<RegisteredApiType>>()
        Files.readAllLines(stateFile, UTF_8).forEach { line ->
            val fields = line.split('\t')
            check(fields.size == 3) { "Invalid Tapik API registry index at '$stateFile'" }
            val source = Path.of(fields[0].decoded()).normalized()
            val type = RegisteredApiType(fields[1].decoded(), ApiInstantiation.valueOf(fields[2]))
            entries.getOrPut(source, ::mutableListOf).add(type)
        }
        return entries
    }

    private fun write(entries: Map<Path, List<RegisteredApiType>>) {
        if (entries.isEmpty()) {
            Files.deleteIfExists(stateFile)
            return
        }
        val lines =
            entries
                .toSortedMap(compareBy(Path::toString))
                .flatMap { (source, types) ->
                    types.map { type ->
                        "${source.toString().encoded()}\t${type.internalName.encoded()}\t${type.instantiation.name}"
                    }
                }
        Files.createDirectories(requireNotNull(stateFile.parent))
        val temporary = Files.createTempFile(stateFile.parent, ".tapik-api-index-", ".tmp")
        try {
            Files.write(temporary, lines, UTF_8)
            moveReplacing(temporary, stateFile)
        } finally {
            Files.deleteIfExists(temporary)
        }
    }
}

internal fun registryIndexPath(outputDirectory: Path): Path =
    outputDirectory.resolveSibling(".${outputDirectory.fileName}.tapik-api-index")

private fun Path.normalized(): Path = toAbsolutePath().normalize()

private val ENCODER: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
private val DECODER: Base64.Decoder = Base64.getUrlDecoder()

private fun String.encoded(): String = ENCODER.encodeToString(toByteArray(UTF_8))

private fun String.decoded(): String = String(DECODER.decode(this), UTF_8)

private fun moveReplacing(source: Path, target: Path) {
    try {
        Files.move(source, target, REPLACE_EXISTING, ATOMIC_MOVE)
    } catch (_: AtomicMoveNotSupportedException) {
        Files.move(source, target, REPLACE_EXISTING)
    }
}
