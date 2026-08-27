package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.common.plugin.KotlinDataField
import dev.akif.tapik.common.plugin.KotlinSealedVariant
import dev.akif.tapik.common.plugin.appendSealedInterface

internal fun StringBuilder.appendResponses(endpoint: WebMvcEndpointModel) {
    appendSealedInterface(
        name = endpoint.responseName,
        variants =
            endpoint.outputs.map { output ->
                KotlinSealedVariant(
                    name = output.variantName,
                    fields = output.fields()
                )
            },
        indentation = "    "
    )
}

private fun WebMvcOutput.fields(): List<KotlinDataField> =
    buildList {
        bodies.firstOrNull()?.let { body ->
            val type = if (allowsNoBody) body.type.asNullable() else body.type
            add(KotlinDataField("body", type, if (allowsNoBody) "null" else null))
        }
        headers.forEach { header ->
            header.name?.let { name -> add(KotlinDataField(name, header.type, header.defaultExpression)) }
        }
    }
