package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationResult
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

internal object GradleGenerationResultCodec {
    fun write(path: Path, result: GenerationResult) {
        path.parent?.let(Files::createDirectories)
        DataOutputStream(Files.newOutputStream(path).buffered()).use { output ->
            output.writeInt(MAGIC)
            output.writeInt(VERSION)
            output.writeInt(result.artifacts.size)
            result.artifacts.forEach { artifact ->
                output.writeString(artifact.relativePath)
                output.writeString(artifact.mediaType)
                output.writeString(artifact.kind.name)
                output.writeString(artifact.content)
                output.writeBoolean(artifact.sharingKey != null)
                artifact.sharingKey?.let(output::writeString)
            }
        }
    }

    fun read(path: Path): GenerationResult =
        DataInputStream(Files.newInputStream(path).buffered()).use { input ->
            require(input.readInt() == MAGIC) { "Invalid tapik Gradle generation result" }
            val version = input.readInt()
            require(version == VERSION) { "Unsupported tapik Gradle generation result version '$version'" }
            val size = input.readInt()
            require(size >= 0) { "Invalid tapik Gradle generation result artifact count '$size'" }
            GenerationResult(
                List(size) {
                    GeneratedArtifact(
                        relativePath = input.readString(),
                        mediaType = input.readString(),
                        kind = ArtifactKind.valueOf(input.readString()),
                        content = input.readString(),
                        sharingKey = if (input.readBoolean()) input.readString() else null
                    )
                }
            )
        }
}

private fun DataOutputStream.writeString(value: String) {
    val bytes = value.toByteArray(UTF_8)
    writeInt(bytes.size)
    write(bytes)
}

private fun DataInputStream.readString(): String {
    val size = readInt()
    require(size >= 0) { "Invalid tapik Gradle generation result string size '$size'" }
    return String(readNBytes(size).also { bytes ->
        require(bytes.size == size) { "Incomplete tapik Gradle generation result" }
    }, UTF_8)
}

private const val MAGIC: Int = 0x54415049
private const val VERSION: Int = 1
