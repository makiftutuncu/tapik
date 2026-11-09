package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CompilerRegistryStateSpec : FunSpec({
    test("identify registry state by target and compilation") {
        compilerRegistryIndexPath("first", "main") shouldBe
            ".gradle/tapik/compiler/id-first/id-main/registry.index"
        compilerRegistryIndexPath("second", "main") shouldBe
            ".gradle/tapik/compiler/id-second/id-main/registry.index"
        compilerRegistryIndexPath("first", "test") shouldBe
            ".gradle/tapik/compiler/id-first/id-test/registry.index"
        compilerRegistrySnapshotDirectoryPath("first", "main") shouldBe
            "tapik/compiler/id-first/id-main/snapshot"
    }

    test("encode registry identities as stable filesystem-safe segments") {
        "Main/é.%_".registryIdentitySegment() shouldBe "id-%4Dain%2F%C3%A9%2E%25_"
    }
})
