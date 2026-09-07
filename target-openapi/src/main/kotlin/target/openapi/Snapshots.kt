package dev.akif.tapik.target.openapi

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Collections

internal fun <Value> Iterable<Value>.snapshotList(): List<Value> =
    Collections.unmodifiableList(toList())

internal fun <Key, Value> Map<Key, Value>.snapshotMap(): Map<Key, Value> =
    Collections.unmodifiableMap(LinkedHashMap(this))

internal fun JsonElement.snapshotJson(): JsonElement =
    when (this) {
        is JsonObject -> JsonObject(mapValues { (_, value) -> value.snapshotJson() }.snapshotMap())
        is JsonArray -> JsonArray(map(JsonElement::snapshotJson).snapshotList())
        is JsonPrimitive -> this
    }
