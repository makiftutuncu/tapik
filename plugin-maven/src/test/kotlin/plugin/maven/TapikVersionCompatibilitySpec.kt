package dev.akif.tapik.plugin.maven

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class TapikVersionCompatibilitySpec : FunSpec({
    test("accept an empty or aligned project Tapik version set") {
        TapikVersionCompatibility.requireCompatible("0.6.0", emptySet())
        TapikVersionCompatibility.requireCompatible("0.6.0", setOf("0.6.0"))
    }

    test("reject project Tapik versions different from the Maven plugin") {
        val error =
            shouldThrow<IllegalArgumentException> {
                TapikVersionCompatibility.requireCompatible("0.6.0", setOf("0.5.0", "0.6.0"))
            }

        error.message shouldContain "Maven plugin uses 0.6.0"
        error.message shouldContain "project dependencies use 0.5.0, 0.6.0"
        error.message shouldContain "Align all dev.akif:tapik-* dependencies"
    }
})
