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

        signature.path.assertType("List", listOf("String"))
        signature.parameters.assertType("Parameters2", listOf("UUID", "Int"))
        signature.parameters.arguments[0].assertType("UUID", emptyList())
        signature.parameters.arguments[1].assertType("Int", emptyList())
        signature.input.type.assertType("Input", listOf("Headers1", "EmptyBody"))
        signature.input.headers.assertType("Headers1", listOf("String"))
        signature.input.body.assertType("EmptyBody", emptyList())
        signature.outputs.assertType("Outputs2", listOf("Output", "Output"))
        val firstOutput = signature.outputs.arguments[0]
        val secondOutput = signature.outputs.arguments[1]
        firstOutput.assertType("Output", listOf("Headers1", "StringBody"))
        secondOutput.assertType("Output", listOf("Headers0", "StringBody"))
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

        signature.path.assertType("List", listOf("String"))
        signature.parameters.assertType("Parameters2", listOf("UUID", "Int"))
        signature.parameters.arguments[0].assertType("UUID", emptyList())
        signature.parameters.arguments[1].assertType("Int", emptyList())
        signature.input.type.assertType("Input", listOf("Headers1", "EmptyBody"))
        signature.input.headers.assertType("Headers1", listOf("String"))
        signature.input.body.assertType("EmptyBody", emptyList())
        signature.outputs.assertType("Outputs2", listOf("Output", "Output"))
        val reflectedFirstOutput = signature.outputs.arguments[0]
        val reflectedSecondOutput = signature.outputs.arguments[1]
        reflectedFirstOutput.assertType("Output", listOf("Headers1", "StringBody"))
        reflectedSecondOutput.assertType("Output", listOf("Headers0", "StringBody"))
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
