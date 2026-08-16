package dev.akif.tapik.plugin.maven.integration

import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.plugin.maven.contract.integration.Authors
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

class MavenUsageSpec : FunSpec({
    test("compile API definitions and generate OpenAPI through one Tapik Maven plugin") {
        val apis = ServiceLoader.load(ApiRegistry::class.java).flatMap(ApiRegistry::apis)
        apis.map { api -> api.id }.sorted() shouldContainExactly listOf("Authors", "Books")
        apis.single { api -> api.id == "Authors" }.shouldBeInstanceOf<Authors>()

        generated("Authors") shouldBe expected("Authors")
        generated("Books") shouldBe expected("Books")
    }
})

private fun generated(api: String): String =
    Files.readString(Path.of("target/generated/tapik/$api.openapi.json"))

private fun expected(api: String): String =
    requireNotNull(MavenUsageSpec::class.java.getResource("/openapi/${api.lowercase()}.json"))
        .readText()
        .trimEnd()
