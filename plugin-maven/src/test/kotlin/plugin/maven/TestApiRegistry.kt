package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.test.fixtures.library.Books

class TestApiRegistry : ApiRegistry {
    override val apis: List<Api> = listOf(Books)
}
