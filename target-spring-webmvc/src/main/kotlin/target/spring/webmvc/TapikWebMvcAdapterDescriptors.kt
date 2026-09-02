package dev.akif.tapik.target.spring.webmvc

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.Properties

internal fun tapikWebMvcAdapterDescriptors(): List<TapikWebMvcAdapterDescriptor> =
    PathMatchingResourcePatternResolver()
        .getResources(ADAPTER_DESCRIPTORS)
        .map { resource ->
            val properties = Properties().apply { resource.inputStream.use(::load) }
            TapikWebMvcAdapterDescriptor(
                handlerType = properties.required(HANDLER_TYPE, resource.description),
                adapterType = properties.required(ADAPTER_TYPE, resource.description)
            )
        }
        .distinct()
        .sortedBy(TapikWebMvcAdapterDescriptor::adapterType)

private fun Properties.required(name: String, resource: String): String =
    requireNotNull(getProperty(name)?.trim()?.takeIf(String::isNotEmpty)) {
        "tapik WebMVC adapter descriptor '$resource' requires '$name'"
    }

private const val ADAPTER_DESCRIPTORS: String = "classpath*:META-INF/tapik/spring/webmvc/*.properties"
private const val HANDLER_TYPE: String = "handler"
private const val ADAPTER_TYPE: String = "adapter"
