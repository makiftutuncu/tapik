package com.example.library.testing

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Document
import org.w3c.dom.NodeList

internal fun seedThirdPartyArtifacts(cache: Path, repository: Path) {
    require(!repository.startsWith(cache)) { "Rehearsal repository must be outside the dependency cache" }
    if (!Files.isDirectory(cache)) return
    Files.walk(cache).use { files ->
        files.filter { file ->
            val relative = cache.relativize(file)
            !relative.startsWith(Path.of("dev", "akif")) && !Files.isSymbolicLink(file) &&
                Files.isRegularFile(file) && (file.toString().endsWith(".jar") || file.toString().endsWith(".pom"))
        }.forEach { file -> copyReleaseFile(file, repository.resolve(cache.relativize(file))) }
    }
}

internal fun stageReleaseArtifacts(root: Path, repository: Path): List<Path> {
    val parent = releasePom(root.resolve("pom.xml"))
    val version = parent.value("/project/version")
    val modules = XPathFactory.newInstance().newXPath()
        .evaluate("/project/modules/module", parent, XPathConstants.NODESET) as NodeList
    val folders = listOf(root) + (0 until modules.length).map { root.resolve(modules.item(it).textContent) }
        .filterNot { it.fileName.toString().startsWith("test-") || it.fileName.toString().startsWith("example-") }
    val staged = mutableListOf<Path>()
    folders.forEach { folder ->
        val pom = releasePom(folder.resolve("pom.xml"))
        val artifact = pom.value("/project/artifactId")
        val destination = repository.resolve("dev/akif/$artifact/$version")
        val prefix = "$artifact-$version"
        if (folder != root && pom.value("/project/packaging") != "pom") {
            val target = folder.resolve("target")
            val main = target.resolve("$prefix.jar")
            val sources = target.resolve("$prefix-sources.jar")
            val docs = target.resolve("$prefix-javadoc.jar")
            listOf(main, sources, docs).forEach { file ->
                check(Files.isRegularFile(file)) { "Missing release artifact $file; run a clean -Prelease build first" }
            }
            ZipFile(sources.toFile()).use { zip ->
                check(zip.entries().asSequence().any { it.name.endsWith(".kt") }) { "Missing Kotlin sources in $sources" }
            }
            ZipFile(docs.toFile()).use { zip ->
                check(zip.getEntry("index.html") != null) { "Missing Dokka HTML index in $docs" }
            }
            val attachments = listOf(main, sources, docs) +
                listOf(target.resolve("$prefix-tests.jar")).filter(Files::isRegularFile)
            attachments.forEach { file ->
                val output = destination.resolve(file.fileName)
                copyReleaseFile(file, output)
                staged.add(output)
            }
        }
        val output = destination.resolve("$prefix.pom")
        copyReleaseFile(folder.resolve("pom.xml"), output)
        staged.add(output)
    }
    return staged
}

internal fun copyReleaseFile(source: Path, destination: Path) {
    Files.createDirectories(destination.parent)
    Files.copy(source, destination)
}

internal fun releasePom(file: Path): Document =
    DocumentBuilderFactory.newInstance().apply {
        setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    }.newDocumentBuilder().parse(file.toFile())

internal fun Document.value(expression: String): String = XPathFactory.newInstance().newXPath().evaluate(expression, this)
