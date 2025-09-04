package dev.akif.tapik

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

interface Generator {
    companion object {
        const val GENERATED_COMMENT = "// This file is auto-generated. Do not edit manually as your changes will be lost."

        val kotlinSourceRoot: List<String> = listOf("src", "main", "kotlin")
    }

    val name: String
    val module: String
    val packages: List<String>
    val fileName: String

    fun generate(limit: Int): String

    fun run(limit: Int, verbose: Boolean) {
        println("Generating $name code up to $limit...")

        val content =
            """|$GENERATED_COMMENT
               |package ${packages.joinToString(separator = ".", prefix = "dev.akif.tapik.").removeSuffix(".")}
               |
               |${generate(limit)}""".trimMargin()

        val filePath = Paths.get((listOf(module) + kotlinSourceRoot + packages + fileName).joinToString(File.separator))
        Files.createDirectories(filePath.parent)

        println("Writing generated code to $filePath...")

        if (verbose) {
            println()
            println(content)
            println()
        }

        Files.writeString(filePath, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}
