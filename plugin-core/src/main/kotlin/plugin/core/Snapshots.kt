package dev.akif.tapik.plugin.core

import java.util.Collections

internal fun <Value> Iterable<Value>.snapshotList(): List<Value> =
    Collections.unmodifiableList(toList())

internal fun <Key, Value> Map<Key, Value>.snapshotMap(): Map<Key, Value> =
    Collections.unmodifiableMap(LinkedHashMap(this))
