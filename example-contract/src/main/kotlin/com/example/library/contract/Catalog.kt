package com.example.library.contract

import dev.akif.tapik.*

/** Catalog operations. */
class Catalog : Api() {
    val list by get(root / "catalog")
}
