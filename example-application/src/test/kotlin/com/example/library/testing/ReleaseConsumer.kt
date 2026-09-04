package com.example.library.testing

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Element

internal fun rehearseRelease(root: Path) {
    val workspace = Files.createTempDirectory("tapik-release-rehearsal-")
    val repository = workspace.resolve("repository")
    val consumer = workspace.resolve("consumer")
    val log = workspace.resolve("consumer-build.log")
    println("tapik release rehearsal: $workspace")
    Files.writeString(root.resolve("example-application/target/release-rehearsal.txt"), "$workspace\n")
    val cache = Path.of(
        System.getProperty("tapik.release.cache", Path.of(System.getProperty("user.home"), ".m2/repository").toString())
    )
    seedThirdPartyArtifacts(cache, repository)
    val artifacts = stageReleaseArtifacts(root, repository)
    Files.writeString(workspace.resolve("staged-artifacts.txt"), artifacts.joinToString("\n", postfix = "\n"))
    Files.copy(
        workspace.resolve("staged-artifacts.txt"),
        root.resolve("example-application/target/release-staged-artifacts.txt"),
        StandardCopyOption.REPLACE_EXISTING
    )
    prepareReleaseConsumer(root, consumer)

    val process = ProcessBuilder(
        root.resolve("mvnw").toString(), "--batch-mode", "--no-transfer-progress",
        "-Dmaven.repo.local=$repository", "clean", "verify"
    ).directory(consumer.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start()
    try {
        if (!process.waitFor(15, TimeUnit.MINUTES)) {
            process.descendants().forEach { it.destroyForcibly() }
            process.destroyForcibly()
            error("External Maven release rehearsal timed out; inspect $log")
        }
    } finally {
        Files.copy(log, root.resolve("example-application/target/release-consumer.log"), StandardCopyOption.REPLACE_EXISTING)
    }
    check(process.exitValue() == 0) {
        "External Maven release rehearsal failed; inspect $log\n${Files.readString(log).takeLast(6000)}"
    }
    check(Files.readString(log).contains("BUILD SUCCESS")) { "Missing consumer build result in $log" }
    val resolved = Files.walk(repository.resolve("dev/akif")).use { files ->
        files.filter { it.toString().endsWith(".jar") || it.toString().endsWith(".pom") }.toList().toSet()
    }
    check(resolved == artifacts.toSet()) { "Consumer resolved unexpected tapik artifacts: ${resolved - artifacts.toSet()}" }
    println("tapik release rehearsal passed: ${artifacts.size} staged files; consumer log: $log")
}

private fun prepareReleaseConsumer(root: Path, consumer: Path) {
    val parent = releasePom(root.resolve("pom.xml"))
    var template = requireNotNull(ReleaseRehearsalSpec::class.java.getResource("/release-consumer/pom.xml")).readText()
    listOf("kotlin.version", "spring-boot.version", "kotest.version", "maven.surefire.version").forEach { property ->
        template = template.replace("@$property@", parent.value("/project/properties/$property"))
    }
    template = template.replace("@version@", parent.value("/project/version"))
    Files.createDirectories(consumer)
    Files.writeString(consumer.resolve("pom.xml"), template)

    listOf("example-contract", "example-application").forEach { module ->
        val source = root.resolve(module)
        val destination = consumer.resolve(module)
        Files.walk(source.resolve("src")).use { files ->
            files.filter(Files::isRegularFile).filter { file ->
                val name = file.fileName.toString()
                !name.startsWith("Release") && name != "BomSpec.kt" &&
                    !file.startsWith(source.resolve("src/test/resources/release-consumer"))
            }.forEach { file -> copyReleaseFile(file, destination.resolve(source.relativize(file))) }
        }
        val pom = releasePom(source.resolve("pom.xml"))
        val parentElement = pom.getElementsByTagName("parent").item(0) as Element
        parentElement.getElementsByTagName("groupId").item(0).textContent = "com.example.library"
        parentElement.getElementsByTagName("artifactId").item(0).textContent = "release-consumer"
        val dependencies = pom.getElementsByTagName("dependency")
        for (index in 0 until dependencies.length) {
            val dependency = dependencies.item(index) as Element
            if (dependency.getElementsByTagName("artifactId").item(0).textContent == "tapik-example-contract") {
                dependency.getElementsByTagName("groupId").item(0).textContent = "com.example.library"
            }
        }
        TransformerFactory.newInstance().newTransformer()
            .transform(DOMSource(pom), StreamResult(destination.resolve("pom.xml").toFile()))
    }
}
