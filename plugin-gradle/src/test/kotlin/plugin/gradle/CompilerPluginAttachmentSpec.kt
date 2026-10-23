package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome

class CompilerPluginAttachmentSpec : FunSpec({
    test("generate an API registry in a Kotlin JVM contract jar") {
        val fixture = GradleContractFixture.create()
        fixture.writeSource(
            """
            package example

            import dev.akif.tapik.Api

            object Books : Api()
            """
        )

        val (_, reused) = repeatWithConfigurationCache { fixture.build("jar") }
        reused.task(":jar")?.outcome shouldBe TaskOutcome.UP_TO_DATE

        val registry = fixture.registryArtifacts()
        registry.classes.shouldHaveSize(1)
        registry.provider shouldBe registry.classes.single().removeSuffix(".class").replace('/', '.')
        fixture.apiIds() shouldContainExactly listOf("Books")
    }

    test("leave no compiler-owned registry when a compilation has no APIs") {
        val fixture = GradleContractFixture.create()
        fixture.writeSource(
            """
            package example

            object NotAnApi
            """
        )

        fixture.build("jar")

        val registry = fixture.registryArtifacts()
        registry.provider.shouldBeNull()
        registry.classes.shouldBeEmpty()
    }

    test("synchronize the registry after non-clean source changes") {
        val fixture = GradleContractFixture.create()
        fixture.writeSource(
            """
            package example

            import dev.akif.tapik.Api

            object Books : Api()
            """
        )
        fixture.build("jar")
        val books = fixture.registryArtifacts()
        fixture.apiIds() shouldContainExactly listOf("Books")

        fixture.writeSource(
            """
            package example

            import dev.akif.tapik.Api

            object Books : Api()
            object Authors : Api()
            """
        )
        fixture.build("jar")
        val booksAndAuthors = fixture.registryArtifacts()
        booksAndAuthors.classes.shouldHaveSize(1)
        (booksAndAuthors.provider == books.provider) shouldBe false
        fixture.apiIds() shouldContainExactly listOf("Books", "Authors")

        fixture.writeSource(
            """
            package example

            import dev.akif.tapik.Api

            object Writers : Api()
            """
        )
        fixture.build("jar")
        val writers = fixture.registryArtifacts()
        writers.classes.shouldHaveSize(1)
        (writers.provider == booksAndAuthors.provider) shouldBe false
        fixture.apiIds() shouldContainExactly listOf("Writers")

        fixture.writeSource(
            """
            package example

            object NotAnApi
            """
        )
        fixture.build("jar")
        val empty = fixture.registryArtifacts()
        empty.provider.shouldBeNull()
        empty.classes.shouldBeEmpty()
    }
})
