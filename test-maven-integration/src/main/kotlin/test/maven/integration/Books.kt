package dev.akif.tapik.test.maven.integration

import dev.akif.tapik.*

object Books : Api() {
    val list by get(root / "books")
}
