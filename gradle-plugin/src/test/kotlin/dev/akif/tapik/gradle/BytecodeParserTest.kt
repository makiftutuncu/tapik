package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.fixtures.SampleEndpoints
import dev.akif.tapik.gradle.metadata.TypeMetadata
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
        assertEquals("dev.akif.tapik.gradle.fixtures", signature.packageName)
        assertEquals("SampleEndpoints", signature.file)
        assertEquals("dev/akif/tapik/gradle/fixtures/SampleEndpoints", signature.ownerInternalName)
        assertEquals("getUser", signature.methodName)

        signature.uriWithParameters.assertType("URIWithParameters2", listOf("UUID", "Int"))
        signature.inputHeaders.assertType("Headers1", listOf("String"))
        signature.outputBodies.assertType("OutputBodies2", listOf("StringBody", "StringBody"))
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
