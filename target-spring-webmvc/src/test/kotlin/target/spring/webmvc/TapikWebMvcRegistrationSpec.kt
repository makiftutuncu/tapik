package dev.akif.tapik.target.spring.webmvc

import io.kotest.core.spec.style.FunSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.UnsatisfiedDependencyException
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary

class TapikWebMvcRegistrationSpec : FunSpec({
    test("expose plain Spring registration") {
        EnableTapikWebMvc::class.java.getAnnotation(Import::class.java).value.toList() shouldBe
            listOf(TapikWebMvcAdapterRegistrar::class)
    }

    test("load generated adapter descriptors") {
        tapikWebMvcAdapterDescriptors() shouldBe
            listOf(
                TapikWebMvcAdapterDescriptor(
                    handlerType = TestHandler::class.java.name,
                    adapterType = TestAdapter::class.java.name
                )
            )
    }

    test("register an adapter for one handler") {
        context(OneHandler::class.java).use { context ->
            context.getBeansOfType(TestAdapter::class.java).values.single().handler.id shouldBe "one"
        }
    }

    test("replace a component-scanned adapter with its canonical registration") {
        context(OneHandler::class.java, scannedAdapter = true).use { context ->
            context.getBeansOfType(TestAdapter::class.java).keys shouldBe setOf(TestAdapter::class.java.name)
        }
    }

    test("fail startup for a component-scanned adapter without a handler") {
        val failure = shouldThrow<UnsatisfiedDependencyException> {
            context(NoHandler::class.java, scannedAdapter = true)
        }
        failure.failureMessages shouldContain TestAdapter::class.java.name
        failure.failureMessages shouldContain TestHandler::class.java.name
        failure.failureMessages shouldContain "expected at least 1 bean"
    }

    test("fail startup without a handler") {
        val failure = shouldThrow<UnsatisfiedDependencyException> { context(NoHandler::class.java) }
        failure.failureMessages shouldContain TestAdapter::class.java.name
        failure.failureMessages shouldContain TestHandler::class.java.name
        failure.failureMessages shouldContain "expected at least 1 bean"
    }

    test("fail startup for ambiguous handlers") {
        val failure = shouldThrow<UnsatisfiedDependencyException> { context(AmbiguousHandlers::class.java) }
        failure.failureMessages shouldContain TestHandler::class.java.name
        failure.failureMessages shouldContain "first"
        failure.failureMessages shouldContain "second"
        failure.failureMessages shouldContain "expected single matching bean"
    }

    test("register an adapter for one primary handler") {
        context(PrimaryHandler::class.java).use { context ->
            context.getBeansOfType(TestAdapter::class.java).values.single().handler.id shouldBe "primary"
        }
    }
})

private val Throwable.failureMessages: String
    get() = generateSequence(this, Throwable::cause).mapNotNull(Throwable::message).joinToString("\n")

private fun context(configuration: Class<*>): AnnotationConfigApplicationContext =
    context(configuration, scannedAdapter = false)

private fun context(
    configuration: Class<*>,
    scannedAdapter: Boolean
): AnnotationConfigApplicationContext =
    AnnotationConfigApplicationContext().apply {
        if (scannedAdapter) {
            registerBeanDefinition(
                "testAdapter",
                RootBeanDefinition(TestAdapter::class.java).apply {
                    autowireMode = RootBeanDefinition.AUTOWIRE_CONSTRUCTOR
                }
            )
        }
        register(configuration)
        refresh()
    }

private interface TestHandler {
    val id: String
}

private data class TestHandlerImplementation(override val id: String) : TestHandler

private class TestAdapter(val handler: TestHandler)

@Configuration(proxyBeanMethods = false)
@EnableTapikWebMvc
private class OneHandler {
    @Bean
    fun handler(): TestHandler = TestHandlerImplementation("one")
}

@Configuration(proxyBeanMethods = false)
@EnableTapikWebMvc
private class NoHandler

@Configuration(proxyBeanMethods = false)
@EnableTapikWebMvc
private class AmbiguousHandlers {
    @Bean
    fun first(): TestHandler = TestHandlerImplementation("first")

    @Bean
    fun second(): TestHandler = TestHandlerImplementation("second")
}

@Configuration(proxyBeanMethods = false)
@EnableTapikWebMvc
private class PrimaryHandler {
    @Bean
    fun first(): TestHandler = TestHandlerImplementation("first")

    @Bean
    @Primary
    fun primary(): TestHandler = TestHandlerImplementation("primary")
}
