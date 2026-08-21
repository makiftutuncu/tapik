package dev.akif.tapik.plugin.compiler

import org.jetbrains.org.objectweb.asm.ClassWriter
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_FINAL
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_SUPER
import org.jetbrains.org.objectweb.asm.Opcodes.AASTORE
import org.jetbrains.org.objectweb.asm.Opcodes.ALOAD
import org.jetbrains.org.objectweb.asm.Opcodes.ANEWARRAY
import org.jetbrains.org.objectweb.asm.Opcodes.ARETURN
import org.jetbrains.org.objectweb.asm.Opcodes.DUP
import org.jetbrains.org.objectweb.asm.Opcodes.GETFIELD
import org.jetbrains.org.objectweb.asm.Opcodes.GETSTATIC
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESTATIC
import org.jetbrains.org.objectweb.asm.Opcodes.NEW
import org.jetbrains.org.objectweb.asm.Opcodes.PUTFIELD
import org.jetbrains.org.objectweb.asm.Opcodes.RETURN
import org.jetbrains.org.objectweb.asm.Opcodes.V1_8
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

internal object RegistryClassWriter {
    fun synchronize(
        outputDirectory: Path,
        apiTypes: List<RegisteredApiType>
    ) {
        removeGeneratedRegistryClasses(outputDirectory)
        val registryInternalName =
            if (apiTypes.isNotEmpty()) {
                registryInternalName(apiTypes)
            } else {
                null
            }
        if (registryInternalName != null) {
            val classFile = outputDirectory.resolve("$registryInternalName.class")
            Files.createDirectories(requireNotNull(classFile.parent))
            Files.write(classFile, classBytes(registryInternalName, apiTypes))
        }
        synchronizeServiceFile(outputDirectory, registryInternalName)
    }

    private fun removeGeneratedRegistryClasses(outputDirectory: Path) {
        val generatedPackage = outputDirectory.resolve(GENERATED_PACKAGE)
        if (!Files.isDirectory(generatedPackage)) return
        Files.list(generatedPackage).use { paths ->
            paths
                .filter { path -> OWNED_CLASS_FILE.matches(path.fileName.toString()) }
                .forEach(Files::deleteIfExists)
        }
    }

    private fun synchronizeServiceFile(
        outputDirectory: Path,
        registryInternalName: String?
    ) {
        val serviceFile = outputDirectory.resolve(SERVICE_PATH)
        val retainedLines =
            if (Files.exists(serviceFile)) {
                Files.readAllLines(serviceFile, UTF_8).filterNot(::isGeneratedProvider)
            } else {
                emptyList()
            }
        val lines = retainedLines + listOfNotNull(registryInternalName?.replace('/', '.'))
        if (lines.isEmpty()) {
            Files.deleteIfExists(serviceFile)
            return
        }
        Files.createDirectories(requireNotNull(serviceFile.parent))
        Files.writeString(serviceFile, lines.joinToString(separator = "\n", postfix = "\n"), UTF_8)
    }

    private fun isGeneratedProvider(line: String): Boolean =
        OWNED_PROVIDER.matches(line.substringBefore('#').trim())

    private fun registryInternalName(apiTypes: List<RegisteredApiType>): String {
        val input = apiTypes.joinToString("\u0000") { "${it.internalName}:${it.instantiation}" }.toByteArray(UTF_8)
        val hash = MessageDigest.getInstance("SHA-256").digest(input).take(8).joinToString("") { byte -> "%02x".format(byte) }
        return "$GENERATED_PACKAGE/TapikApiRegistry_$hash"
    }

    private fun classBytes(
        registryInternalName: String,
        apiTypes: List<RegisteredApiType>
    ): ByteArray {
        val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
        writer.visit(
            V1_8,
            ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
            registryInternalName,
            null,
            "java/lang/Object",
            arrayOf(API_REGISTRY_INTERNAL_NAME)
        )
        writer.visitField(
            ACC_PRIVATE or ACC_FINAL,
            APIS_FIELD,
            "Ljava/util/List;",
            "Ljava/util/List<L$API_INTERNAL_NAME;>;",
            null
        ).visitEnd()
        writer.visitConstructor(registryInternalName, apiTypes)
        writer.visitApisGetter(registryInternalName)
        writer.visitEnd()
        return writer.toByteArray()
    }

    private fun ClassWriter.visitConstructor(
        registryInternalName: String,
        apiTypes: List<RegisteredApiType>
    ) {
        val constructor = visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        constructor.visitCode()
        constructor.visitVarInsn(ALOAD, 0)
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        constructor.visitVarInsn(ALOAD, 0)
        constructor.visitLdcInsn(apiTypes.size)
        constructor.visitTypeInsn(ANEWARRAY, API_INTERNAL_NAME)
        apiTypes.forEachIndexed { index, apiType ->
            constructor.visitInsn(DUP)
            constructor.visitLdcInsn(index)
            when (apiType.instantiation) {
                ApiInstantiation.OBJECT ->
                    constructor.visitFieldInsn(
                        GETSTATIC,
                        apiType.internalName,
                        "INSTANCE",
                        "L${apiType.internalName};"
                    )
                ApiInstantiation.CONSTRUCTOR -> {
                    constructor.visitTypeInsn(NEW, apiType.internalName)
                    constructor.visitInsn(DUP)
                    constructor.visitMethodInsn(INVOKESPECIAL, apiType.internalName, "<init>", "()V", false)
                }
            }
            constructor.visitInsn(AASTORE)
        }
        constructor.visitMethodInsn(
            INVOKESTATIC,
            "java/util/Arrays",
            "asList",
            "([Ljava/lang/Object;)Ljava/util/List;",
            false
        )
        constructor.visitFieldInsn(PUTFIELD, registryInternalName, APIS_FIELD, "Ljava/util/List;")
        constructor.visitInsn(RETURN)
        constructor.visitMaxs(0, 0)
        constructor.visitEnd()
    }

    private fun ClassWriter.visitApisGetter(registryInternalName: String) {
        val getter =
            visitMethod(
                ACC_PUBLIC,
                "getApis",
                "()Ljava/util/List;",
                "()Ljava/util/List<L$API_INTERNAL_NAME;>;",
                null
        )
        getter.visitCode()
        getter.visitVarInsn(ALOAD, 0)
        getter.visitFieldInsn(GETFIELD, registryInternalName, APIS_FIELD, "Ljava/util/List;")
        getter.visitInsn(ARETURN)
        getter.visitMaxs(0, 0)
        getter.visitEnd()
    }

}

private const val API_INTERNAL_NAME: String = "dev/akif/tapik/Api"
private const val API_REGISTRY_INTERNAL_NAME: String = "dev/akif/tapik/ApiRegistry"
private const val APIS_FIELD: String = "apis"
private const val GENERATED_PACKAGE: String = "dev/akif/tapik/generated"
private const val SERVICE_PATH: String = "META-INF/services/dev.akif.tapik.ApiRegistry"
private val OWNED_PROVIDER: Regex =
    Regex("dev\\.akif\\.tapik\\.generated\\.TapikApiRegistry(?:_[0-9a-f]{16})?")
private val OWNED_CLASS_FILE: Regex = Regex("TapikApiRegistry(?:_[0-9a-f]{16})?\\.class")
