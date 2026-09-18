package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.*
import dev.akif.tapik.common.plugin.GenerationRequest
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.cli.common.ExitCode
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.util.DefaultUriBuilderFactory
import java.net.URLClassLoader

class RestClientUriSpec : FunSpec({
    test("retain encoded base components and declaration order when appending request data") {
        val factory = DefaultUriBuilderFactory("https://library.example/base%20path/?tenant=a%2Bb#part%20one")
            .apply { encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE }
        val definition = root / path.remaining("rest") + query.string("filter+name") + query.string("second")

        val uri = restClientUri(
            builder = factory.builder(),
            uri = definition,
            pathValues = mapOf("rest" to "draft reports/a+b"),
            queryValues = linkedMapOf("second" to listOf("two"), "filter+name" to listOf("a+b"))
        )

        uri.toASCIIString() shouldBe
            "https://library.example/base%20path/draft%20reports/a%2Bb?tenant=a%2Bb&filter%2Bname=a%2Bb&second=two#part%20one"
    }

    test("generated requests preserve encoded literals and encode raw values exactly once") {
        val source = RestClientTarget.generate(GenerationRequest(apis = listOf(EncodedPaths))).artifacts.single().content
        val compilation = compileKotlin(source + """

            public class EncodedPathsImplementation(
                override val restClientTransport: dev.akif.tapik.target.spring.restclient.RestClientTransport
            ) : EncodedPathsClient {
                override val encodedPathsApi = dev.akif.tapik.target.spring.restclient.EncodedPaths
            }
        """.trimIndent())
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        val builder = RestClient.builder().baseUrl("https://library.example/base?tenant=library#section")
        val server = MockRestServiceServer.bindTo(builder).build()
        server.expect(requestTo("https://library.example/base/a%20b/%2f%3F%23%25?tenant=library#section"))
            .andRespond(withSuccess())
        val expectedPath = "https://library.example/base/a%20b/%2f%3F%23%25/" +
            "a%2Fb%20%2B%25%7Bx%7D/draft%20reports/a%2Bb/%252F/%C3%A7"
        server.expect(requestTo(expectedPath + "?tenant=library&q=a%2Bb%20%26%3D%2520&tag=x%2Fy&tag=%7Bz%7D&page=1#section"))
            .andRespond(withSuccess())
        server.expect(requestTo(expectedPath + "?tenant=library&q=plain&page=2#section"))
            .andRespond(withSuccess())
        server.expect(requestTo("https://library.example/base/?tenant=library#section"))
            .andRespond(withSuccess())

        URLClassLoader(arrayOf(compilation.outputDirectory.toUri().toURL()), EncodedPaths::class.java.classLoader).use { loader ->
            val type = loader.loadClass(
                "dev.akif.tapik.generated.target.spring.restclient.EncodedPaths.EncodedPathsImplementation"
            )
            val client = type.getConstructor(RestClientTransport::class.java).newInstance(RestClientTransport(builder.build()))
            type.getMethod("literal").invoke(client)
            val download = type.getMethod("download", String::class.java, List::class.java, String::class.java, List::class.java, Int::class.javaPrimitiveType)
            val remaining = listOf("draft reports", "a+b", "%2F", "ç")
            download.invoke(client, "a/b +%{x}", remaining, "a+b &=%20", listOf("x/y", "{z}"), 1)
            download.invoke(client, "a/b +%{x}", remaining, "plain", null, 2)
            type.getMethod("index").invoke(client)
        }
        server.verify()
    }
})

public object EncodedPaths : Api() {
    public val download by get(
        root / "a%20b/%2f%3F%23%25" / path.string("id") / path.remaining("remaining") +
            query.string("q") + query.string("tag").repeated().optional() + query.int("page").optional(default = 1)
    )
    public val literal by get(root / "a%20b/%2f%3F%23%25")
    public val index by get(root)
}
