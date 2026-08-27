package dev.akif.tapik.test.maven.contract

import dev.akif.tapik.*

/** Catalog operations published for multi-API generation tests. */
class Catalog : Api() {
    val list by get(root / "catalog")
}
