package dev.akif.tapik.plugin.spring.webmvc

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.DeferredImportSelector
import org.springframework.context.annotation.Import
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata

/** Registers generated Tapik WebMVC adapters in Spring Boot applications. */
@AutoConfiguration
@Import(TapikWebMvcImportSelector::class)
class TapikWebMvcAutoConfiguration

internal class TapikWebMvcImportSelector : DeferredImportSelector {
    override fun selectImports(importingClassMetadata: AnnotationMetadata): Array<String> =
        PathMatchingResourcePatternResolver()
            .getResources(ADAPTER_IMPORTS)
            .asSequence()
            .flatMap { resource -> resource.inputStream.bufferedReader().use { reader -> reader.readLines() } }
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinct()
            .sorted()
            .toList()
            .toTypedArray()
}

private const val ADAPTER_IMPORTS: String = "classpath*:META-INF/tapik/spring/webmvc/*.imports"
