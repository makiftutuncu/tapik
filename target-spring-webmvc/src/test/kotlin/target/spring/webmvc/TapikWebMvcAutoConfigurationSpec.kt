package dev.akif.tapik.target.spring.webmvc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.springframework.core.type.AnnotationMetadata

class TapikWebMvcAutoConfigurationSpec : FunSpec({
    test("publish the Boot auto-configuration entry") {
        requireNotNull(
            TapikWebMvcAutoConfigurationSpec::class.java.getResource(
                "/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
            )
        ).readText().lineSequence().filter(String::isNotBlank).toList() shouldContainExactly
            listOf(TapikWebMvcAutoConfiguration::class.java.name)
    }

    test("import generated adapters in canonical order") {
        TapikWebMvcImportSelector()
            .selectImports(AnnotationMetadata.introspect(TapikWebMvcAutoConfiguration::class.java))
            .toList() shouldContainExactly
            listOf("example.generated.AuthorsGeneratedController", "example.generated.BooksGeneratedController")
    }
})
