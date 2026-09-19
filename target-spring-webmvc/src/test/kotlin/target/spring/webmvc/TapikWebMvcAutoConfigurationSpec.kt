package dev.akif.tapik.target.spring.webmvc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.UnsatisfiedDependencyException
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration

class TapikWebMvcAutoConfigurationSpec : FunSpec({
    test("publish the Boot auto-configuration entry") {
        requireNotNull(
            TapikWebMvcAutoConfigurationSpec::class.java.getResource(
                "/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
            )
        ).readText().lineSequence().filter(String::isNotBlank).toList() shouldContainExactly
            listOf(TapikWebMvcAutoConfiguration::class.java.name)
        TapikWebMvcAutoConfiguration::class.java.isAnnotationPresent(EnableTapikWebMvc::class.java) shouldBe true
    }

    test("fail Boot auto-configuration when a generated handler is missing") {
        val failure = shouldThrow<UnsatisfiedDependencyException> {
            AnnotationConfigApplicationContext(BootWithoutHandler::class.java)
        }
        val messages = generateSequence(failure as Throwable, Throwable::cause)
            .mapNotNull(Throwable::message)
            .joinToString("\n")

        messages shouldContain "dev.akif.tapik.target.spring.webmvc.TestAdapter"
        messages shouldContain "dev.akif.tapik.target.spring.webmvc.TestHandler"
    }
})

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
private class BootWithoutHandler
