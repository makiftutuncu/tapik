package dev.akif.app

fun main() {
    val documentation = MarkdownDocumentationInterpreter(Users.api).generate()
    println(documentation)
}
