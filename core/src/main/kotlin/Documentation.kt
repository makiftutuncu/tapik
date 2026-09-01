package dev.akif.tapik

/**
 * Human-readable endpoint documentation.
 *
 * @property summary short endpoint summary.
 * @property description detailed endpoint description.
 * @throws IllegalArgumentException when a present value is blank.
 */
data class EndpointDocumentation(
    val summary: String? = null,
    val description: String? = null
) {
    init {
        require(summary == null || summary.isNotBlank()) { "Endpoint summary must not be blank" }
        require(description == null || description.isNotBlank()) { "Endpoint description must not be blank" }
    }
}

/**
 * OpenAPI-aligned documentation for a path, query, or header parameter.
 *
 * @property description optional detailed description.
 * @property deprecated whether consumers should avoid the parameter.
 * @throws IllegalArgumentException when [description] is present but blank.
 */
data class ParameterDocumentation(
    val description: String? = null,
    val deprecated: Boolean = false
) {
    init {
        requireNonBlankDocumentation(description, "Parameter description")
    }
}

/**
 * OpenAPI-aligned documentation for a complete request body.
 *
 * @property description optional detailed description.
 * @throws IllegalArgumentException when [description] is present but blank.
 */
data class RequestBodyDocumentation(
    val description: String? = null
) {
    init {
        requireNonBlankDocumentation(description, "Request body description")
    }
}

/**
 * OpenAPI-aligned documentation for one response alternative.
 *
 * @property description optional detailed description overriding the target's status-derived fallback.
 * @throws IllegalArgumentException when [description] is present but blank.
 */
data class ResponseDocumentation(
    val description: String? = null
) {
    init {
        requireNonBlankDocumentation(description, "Response description")
    }
}

private fun requireNonBlankDocumentation(value: String?, name: String) {
    require(value == null || value.isNotBlank()) { "$name must not be blank" }
}

/** Replaces this draft endpoint's summary. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.summary(
        summary: String
    ): Endpoint<P, Q, H, I, O, Draft> = document(summary = summary)

/** Replaces this draft endpoint's description. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.description(
        description: String
    ): Endpoint<P, Q, H, I, O, Draft> = document(description = description)

/** Replaces the supplied documentation values while retaining omitted values. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.document(
        summary: String? = documentation.summary,
        description: String? = documentation.description
    ): Endpoint<P, Q, H, I, O, Draft> = copy(documentation = EndpointDocumentation(summary, description))
