package com.example.library.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.w3c.dom.Element
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

class BomSpec : FunSpec({
    test("manage every published tapik module at the release version") {
        val repository = repositoryRoot()
        val productionArtifacts =
            elements(repository.resolve("pom.xml"), "module")
                .map(Element::getTextContent)
                .filterNot { module ->
                    module == "bom" || module.startsWith("test-") || module.startsWith("example-")
                }
                .map { module -> documentElement(repository.resolve(module).resolve("pom.xml")).childText("artifactId") }

        val managedDependencies = elements(repository.resolve("bom/pom.xml"), "dependency")
        val managedArtifacts = managedDependencies.map { dependency -> dependency.childText("artifactId") }

        managedArtifacts shouldContainExactlyInAnyOrder productionArtifacts
        managedDependencies.forEach { dependency -> dependency.childText("version") shouldBe $$"${project.version}" }
    }
})

private fun repositoryRoot(): Path =
    generateSequence(Path.of("").toAbsolutePath().normalize(), Path::getParent)
        .first { path -> Files.isRegularFile(path.resolve("bom/pom.xml")) }

private fun elements(path: Path, name: String): List<Element> {
    val elements = documentElement(path).getElementsByTagName(name)
    return List(elements.length) { index -> elements.item(index) as Element }
}

private fun documentElement(path: Path): Element =
    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile()).documentElement

private fun Element.childText(name: String): String =
    childNodes
        .let { nodes -> List(nodes.length, nodes::item) }
        .filterIsInstance<Element>()
        .single { child -> child.tagName == name }
        .textContent
