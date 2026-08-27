package dev.akif.tapik.common.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KotlinImportsSpec : FunSpec({
    test("optimize unambiguous imports without changing literals or comments") {
        val source =
            """
            package generated

            import dev.akif.tapik.common.spring.toSpringMediaType
            import java.time.Instant as Moment

            @org.springframework.web.bind.annotation.RestController
            public class UUID {
                public fun load(
                    id: java.util.UUID,
                    at: java.time.Instant,
                    first: alpha.model.Book,
                    second: beta.model.Book,
                    bytes: kotlin.ByteArray,
                    local: generated.LocalType
                ): dev.akif.tapik.DecodeResult.Success {
                    val literal = "java.time.Instant"
                    // org.springframework.http.MediaType
                    return dev.akif.tapik.DecodeResult.Success(first)
                }
            }
            """.trimIndent()

        source.optimizeKotlinImports("generated") shouldBe
            """
            package generated

            import dev.akif.tapik.DecodeResult
            import dev.akif.tapik.common.spring.toSpringMediaType
            import java.time.Instant as Moment
            import org.springframework.web.bind.annotation.RestController

            @RestController
            public class UUID {
                public fun load(
                    id: java.util.UUID,
                    at: Moment,
                    first: alpha.model.Book,
                    second: beta.model.Book,
                    bytes: ByteArray,
                    local: LocalType
                ): DecodeResult.Success {
                    val literal = "java.time.Instant"
                    // org.springframework.http.MediaType
                    return DecodeResult.Success(first)
                }
            }
            """.trimIndent()
    }
})
