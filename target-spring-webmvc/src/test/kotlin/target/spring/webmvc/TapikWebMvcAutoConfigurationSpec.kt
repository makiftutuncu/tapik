package dev.akif.tapik.target.spring.webmvc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

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
})
