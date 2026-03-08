package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.fixtures.SampleEndpoints
import dev.akif.tapik.plugin.metadata.TypeMetadata
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

class BytecodeParserTest {
    @Test
    fun `parseHttpEndpoint extracts metadata from compiled endpoint`() {
        val classBytes = readClassBytes(SampleEndpoints::class.java)
        val classNode = ClassNode()
        ClassReader(classBytes).accept(classNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

        val method =
            classNode.methods.firstOrNull { it.name == "getUser" }
                ?: error("Expected getter for 'user' endpoint")

        val signature =
            BytecodeParser.parseHttpEndpoint(
                signature = method.signature ?: error("Method signature is missing"),
                ownerInternalName = classNode.name,
                methodName = method.name
            )

        assertNotNull(signature)
        assertEquals("user", signature.name)
        assertEquals("dev.akif.tapik.plugin.fixtures", signature.packageName)
        assertEquals("SampleEndpoints", signature.file)
        assertEquals("dev/akif/tapik/plugin/fixtures/SampleEndpoints", signature.ownerInternalName)
        assertEquals("getUser", signature.methodName)

        signature.path.assertType("kotlin.collections.List", listOf("kotlin.String"))
        signature.parameters.assertType("dev.akif.tapik.Parameters2", listOf("java.util.UUID", "kotlin.Int"))
        signature.parameters.arguments[0].assertType("java.util.UUID", emptyList())
        signature.parameters.arguments[1].assertType("kotlin.Int", emptyList())
        signature.input.type.assertType("dev.akif.tapik.Input", listOf("dev.akif.tapik.Headers1", "dev.akif.tapik.EmptyBody"))
        signature.input.headers.assertType("dev.akif.tapik.Headers1", listOf("kotlin.String"))
        signature.input.body.assertType("dev.akif.tapik.EmptyBody", emptyList())
        signature.outputs.assertType("dev.akif.tapik.Outputs2", listOf("dev.akif.tapik.Output", "dev.akif.tapik.Output"))
        val firstOutput = signature.outputs.arguments[0]
        val secondOutput = signature.outputs.arguments[1]
        firstOutput.assertType("dev.akif.tapik.Output", listOf("dev.akif.tapik.Headers1", "dev.akif.tapik.StringBody"))
        secondOutput.assertType("dev.akif.tapik.Output", listOf("dev.akif.tapik.Headers0", "dev.akif.tapik.StringBody"))
    }

    @Test
    fun `parseHttpEndpoint extracts metadata from reflection return type`() {
        val owner = SampleEndpoints::class.java
        val method = owner.getDeclaredMethod("getUser")

        val signature =
            BytecodeParser.parseHttpEndpoint(
                returnType = method.genericReturnType,
                ownerInternalName = owner.name.replace('.', '/'),
                methodName = method.name
            )

        assertNotNull(signature)
        assertEquals("user", signature.name)
        assertEquals("dev.akif.tapik.plugin.fixtures", signature.packageName)
        assertEquals("SampleEndpoints", signature.file)
        assertEquals("dev/akif/tapik/plugin/fixtures/SampleEndpoints", signature.ownerInternalName)
        assertEquals("getUser", signature.methodName)

        signature.path.assertType("kotlin.collections.List", listOf("kotlin.String"))
        signature.parameters.assertType("dev.akif.tapik.Parameters2", listOf("java.util.UUID", "kotlin.Int"))
        signature.parameters.arguments[0].assertType("java.util.UUID", emptyList())
        signature.parameters.arguments[1].assertType("kotlin.Int", emptyList())
        signature.input.type.assertType("dev.akif.tapik.Input", listOf("dev.akif.tapik.Headers1", "dev.akif.tapik.EmptyBody"))
        signature.input.headers.assertType("dev.akif.tapik.Headers1", listOf("kotlin.String"))
        signature.input.body.assertType("dev.akif.tapik.EmptyBody", emptyList())
        signature.outputs.assertType("dev.akif.tapik.Outputs2", listOf("dev.akif.tapik.Output", "dev.akif.tapik.Output"))
        val reflectedFirstOutput = signature.outputs.arguments[0]
        val reflectedSecondOutput = signature.outputs.arguments[1]
        reflectedFirstOutput.assertType("dev.akif.tapik.Output", listOf("dev.akif.tapik.Headers1", "dev.akif.tapik.StringBody"))
        reflectedSecondOutput.assertType("dev.akif.tapik.Output", listOf("dev.akif.tapik.Headers0", "dev.akif.tapik.StringBody"))
    }

    private fun readClassBytes(clazz: Class<*>): ByteArray {
        val resourcePath = clazz.name.replace('.', '/') + ".class"
        return clazz.classLoader.getResourceAsStream(resourcePath)?.use { it.readAllBytes() }
            ?: error("Cannot load class bytes for $resourcePath")
    }

    private fun TypeMetadata.assertType(
        expectedName: String,
        expectedArgumentNames: List<String>
    ) {
        assertEquals(expectedName, name)
        assertEquals(expectedArgumentNames, arguments.map { it.name })
    }
}
