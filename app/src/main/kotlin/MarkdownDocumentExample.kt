package dev.akif.app

import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val generator = MarkdownDocumentationInterpreter(Users.api)
    val docs = generator.generate()
    Files.write(Path.of("docs.md"), docs.lines())
}
