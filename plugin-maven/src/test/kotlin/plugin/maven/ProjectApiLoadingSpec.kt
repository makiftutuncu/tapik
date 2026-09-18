package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceConfigurationError

class ProjectApiLoadingSpec : FunSpec({
    test("diagnose a malformed registry provider on the project classpath") {
        val failure = shouldThrow<IllegalStateException> {
            withRegistry("example.MissingApiRegistry")
        }

        failure.message shouldContain "Failed to load API registries from the project classpath"
        failure.message shouldContain "runtime dependencies"
        failure.message shouldContain "compatible tapik versions"
        (failure.cause is ServiceConfigurationError) shouldBe true
        failure.cause!!.message shouldContain "example.MissingApiRegistry"
    }

    test("retain service construction failure and restore the thread context class loader") {
        val previous = Thread.currentThread().contextClassLoader
        val failure = shouldThrow<IllegalStateException> {
            withRegistry(ConstructionFailureApiRegistry::class.java.name)
        }

        failure.message shouldContain "project classpath"
        (failure.cause is ServiceConfigurationError) shouldBe true
        failure.cause!!.cause!!.message shouldBe "registry construction failed"
        Thread.currentThread().contextClassLoader shouldBe previous
    }

    test("diagnose missing and incompatible linked types while reading registry APIs") {
        listOf(
            MissingDependencyApiRegistry::class.java.name to NoClassDefFoundError::class.java,
            IncompatibleDependencyApiRegistry::class.java.name to NoSuchMethodError::class.java
        ).forEach { (provider, errorType) ->
            val previous = Thread.currentThread().contextClassLoader
            val failure = shouldThrow<IllegalStateException> { withRegistry(provider) }

            failure.message shouldContain "project classpath"
            failure.message shouldContain "compiled contract artifacts"
            failure.message shouldContain "runtime dependencies"
            failure.message shouldContain "compatible tapik versions"
            failure.cause!!.javaClass shouldBe errorType
            Thread.currentThread().contextClassLoader shouldBe previous
        }
    }

    test("do not translate arbitrary errors from registry access") {
        val previous = Thread.currentThread().contextClassLoader
        val failure = shouldThrow<AssertionError> { withRegistry(AssertionFailureApiRegistry::class.java.name) }

        failure.message shouldBe "registry assertion failed"
        Thread.currentThread().contextClassLoader shouldBe previous
    }

    test("do not mislabel linkage failures from the subsequent generation block") {
        val original = NoClassDefFoundError("example/TargetDependency")
        val previous = Thread.currentThread().contextClassLoader
        val failure = shouldThrow<NoClassDefFoundError> {
            ProjectApis.use(emptyList(), ProjectApiLoadingSpec::class.java.classLoader) { _, _ -> throw original }
        }

        failure shouldBe original
        Thread.currentThread().contextClassLoader shouldBe previous
    }
})

private fun withRegistry(provider: String) {
    ProjectApis.use(
        listOf(apiProviderDirectory(provider)),
        ProjectApiLoadingSpec::class.java.classLoader
    ) { _, _ -> error("generation must not start after registry loading fails") }
}

internal fun apiProviderDirectory(provider: String): Path {
    val directory = Files.createTempDirectory("tapik-api-provider-")
    val service = directory.resolve("META-INF/services/dev.akif.tapik.ApiRegistry")
    Files.createDirectories(service.parent)
    Files.writeString(service, "$provider\n")
    return directory
}

class ConstructionFailureApiRegistry : ApiRegistry {
    init {
        error("registry construction failed")
    }

    override val apis: List<Api> = emptyList()
}

class MissingDependencyApiRegistry : ApiRegistry {
    override val apis: List<Api>
        get() = throw NoClassDefFoundError("example/ContractDependency")
}

class IncompatibleDependencyApiRegistry : ApiRegistry {
    override val apis: List<Api>
        get() = throw NoSuchMethodError("example.ContractDependency.api()")
}

class AssertionFailureApiRegistry : ApiRegistry {
    override val apis: List<Api>
        get() = throw AssertionError("registry assertion failed")
}
