package dev.akif.tapik.plugin.maven.contract.integration

import dev.akif.tapik.*

class Authors : Api() {
    val list by get(root / "authors")
}
