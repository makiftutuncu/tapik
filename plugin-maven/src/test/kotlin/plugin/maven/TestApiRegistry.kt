package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.test.fixtures.library.Authors
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.test.fixtures.library.Rentals
import java.nio.file.Path

class TestApiRegistry : ApiRegistry {
    override val apis: List<Api> = listOf(Books, Authors, Rentals)
}

internal val testApiClasspath: List<Path> =
    listOf(Path.of(TestApiRegistry::class.java.protectionDomain.codeSource.location.toURI()))
