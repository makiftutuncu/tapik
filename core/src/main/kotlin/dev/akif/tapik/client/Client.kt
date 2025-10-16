package dev.akif.tapik.client

import dev.akif.tapik.http.HeaderValues
import dev.akif.tapik.http.QueryParameter

interface Client {
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asQueryParameter(): QueryParameter<T> =
        this as? QueryParameter<T> ?: error("Expected QueryParameter but got ${this?.let { it::class.java.name }}")

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asHeaderValues(): HeaderValues<T> =
        this as? HeaderValues<T> ?: error("Expected HeaderValues but got ${this?.let { it::class.java.name }}")

    fun <T : Any> QueryParameter<T>.getDefaultOrFail(): T =
        requireNotNull(default) { "Default value of query parameter '$name' is missing" }

    fun <T : Any> HeaderValues<T>.getFirstValueOrFail(): T =
        requireNotNull(values.firstOrNull()) { "First value of header '$name' is missing" }
}
