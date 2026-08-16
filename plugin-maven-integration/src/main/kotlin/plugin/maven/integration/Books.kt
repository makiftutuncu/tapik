package dev.akif.tapik.plugin.maven.integration

import dev.akif.tapik.*

object Books : Api() {
    val list by get(root / "books")
}
