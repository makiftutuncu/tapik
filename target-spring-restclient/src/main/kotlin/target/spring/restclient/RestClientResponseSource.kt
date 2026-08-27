package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.common.plugin.KotlinDataField
import dev.akif.tapik.common.plugin.KotlinSealedVariant
import dev.akif.tapik.common.plugin.appendSealedInterface

internal fun StringBuilder.appendResponse(endpoint: RestClientEndpointModel) {
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

private fun RestClientOutput.fields(): List<KotlinDataField> =
    buildList {
        bodies.firstOrNull()?.let { body ->
            val type = if (allowsNoBody) body.type.asNullable() else body.type
            add(KotlinDataField("body", type))
        }
        headers.forEach { header -> add(KotlinDataField(header.name, header.type)) }
    }
