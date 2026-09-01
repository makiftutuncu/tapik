package dev.akif.tapik.plugin.maven

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.nio.file.Files

class GeneratedOutputSynchronizerSpec : FunSpec({
    test("replace stale outputs owned by one execution") {
        val workspace = Files.createTempDirectory("tapik-generated-output-").apply { toFile().deleteOnExit() }
        val staging = workspace.resolve("staging")
        val output = workspace.resolve("classes")
        val state = workspace.resolve("state")
        Files.createDirectories(staging.resolve("example"))
        Files.writeString(staging.resolve("example/OldServer.class"), "old")
        Files.createDirectories(staging.resolve("META-INF/tapik"))
        Files.writeString(staging.resolve("META-INF/tapik/old.properties"), "old")

        GeneratedOutputSynchronizer.synchronize(staging, output, state, "webmvc")

        Files.delete(staging.resolve("example/OldServer.class"))
        Files.delete(staging.resolve("META-INF/tapik/old.properties"))
        Files.writeString(staging.resolve("example/NewServer.class"), "new")
        Files.writeString(staging.resolve("META-INF/tapik/new.properties"), "new")
        GeneratedOutputSynchronizer.synchronize(staging, output, state, "webmvc")

        Files.exists(output.resolve("example/OldServer.class")) shouldBe false
        Files.exists(output.resolve("META-INF/tapik/old.properties")) shouldBe false
        Files.readString(output.resolve("example/NewServer.class")) shouldBe "new"
        Files.readString(output.resolve("META-INF/tapik/new.properties")) shouldBe "new"
    }

    test("preserve outputs belonging to other executions and the application") {
        val workspace = Files.createTempDirectory("tapik-generated-output-shared-").apply { toFile().deleteOnExit() }
        val first = Files.createDirectories(workspace.resolve("first/example"))
        val second = Files.createDirectories(workspace.resolve("second/example"))
        val output = Files.createDirectories(workspace.resolve("classes/example"))
        val state = workspace.resolve("state")
        Files.writeString(first.resolve("First.class"), "first")
        Files.writeString(second.resolve("Second.class"), "second")
        Files.writeString(output.resolve("Application.class"), "application")

        GeneratedOutputSynchronizer.synchronize(workspace.resolve("first"), workspace.resolve("classes"), state, "first")
        GeneratedOutputSynchronizer.synchronize(workspace.resolve("second"), workspace.resolve("classes"), state, "second")
        GeneratedOutputSynchronizer.synchronize(
            Files.createDirectories(workspace.resolve("first-empty")),
            workspace.resolve("classes"),
            state,
            "first"
        )

        Files.exists(output.resolve("First.class")) shouldBe false
        Files.readString(output.resolve("Second.class")) shouldBe "second"
        Files.readString(output.resolve("Application.class")) shouldBe "application"
    }

    test("reject output paths owned by another execution") {
        val workspace = Files.createTempDirectory("tapik-generated-output-conflict-").apply { toFile().deleteOnExit() }
        val first = Files.createDirectories(workspace.resolve("first/example"))
        val second = Files.createDirectories(workspace.resolve("second/example"))
        val output = workspace.resolve("classes")
        val state = workspace.resolve("state")
        Files.writeString(first.resolve("Server.class"), "first")
        Files.writeString(second.resolve("Server.class"), "second")
        GeneratedOutputSynchronizer.synchronize(workspace.resolve("first"), output, state, "first")

        shouldThrow<IllegalStateException> {
            GeneratedOutputSynchronizer.synchronize(workspace.resolve("second"), output, state, "second")
        }.message shouldContain "'example/Server.class' by 'first'"
        Files.readString(output.resolve("example/Server.class")) shouldBe "first"
    }
})
