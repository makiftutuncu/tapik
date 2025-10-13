package dev.akif.tapik.gradle

import org.gradle.api.logging.Logger
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.MultiANewArrayInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.VarInsnNode

private const val CLINIT_METHOD_NAME = "<clinit>"

internal data class MethodKey(
    val ownerInternalName: String,
    val name: String,
    val descriptor: String
) {
    override fun toString(): String = "$ownerInternalName#$name$descriptor"
}

internal data class EndpointMetadata(
    val parameterNames: List<String?>,
    val inputHeaderNames: List<String?>,
    val outputHeaderNames: List<String?>
)

internal class EndpointMetadataAnalyzer(
    private val logger: Logger,
    private val classNodeProvider: (String) -> ClassNode?
) {
    private val externalRegistry = ExternalFieldRegistry()

    fun analyze(classNode: ClassNode): Map<MethodKey, EndpointMetadata> {
        val registry = NameRegistry()
        classNode.methods
            .firstOrNull { it.name == CLINIT_METHOD_NAME }
            ?.let { interpretMethod(classNode.name, it, registry, null) }

        val result = LinkedHashMap<MethodKey, EndpointMetadata>()
        classNode.methods.forEach { method ->
            val collector = EndpointCollector()
            interpretMethod(classNode.name, method, registry, collector)
            if (collector.isNotEmpty()) {
                result[MethodKey(classNode.name, method.name, method.desc)] = collector.toMetadata()
            }
        }
        return result
    }

    private fun interpretMethod(
        ownerInternalName: String,
        method: MethodNode,
        registry: NameRegistry,
        collector: EndpointCollector?
    ) {
        if (method.instructions == null) {
            return
        }

        val interpreter = InstructionInterpreter(
            ownerInternalName = ownerInternalName,
            registry = registry,
            collector = collector,
            externalRegistry = externalRegistry,
            classNodeProvider = classNodeProvider,
            logger = logger
        )
        interpreter.process(method)
    }

    private class EndpointCollector {
        private val parameterNames = ArrayList<String?>()
        private val inputHeaderNames = ArrayList<String?>()
        private val outputHeaderNames = ArrayList<String?>()

        fun addParameter(name: String?) {
            parameterNames += name
        }

        fun addInputHeader(name: String?) {
            inputHeaderNames += name
        }

        fun addOutputHeader(name: String?) {
            outputHeaderNames += name
        }

        fun isNotEmpty(): Boolean =
            parameterNames.isNotEmpty() ||
                inputHeaderNames.isNotEmpty() ||
                outputHeaderNames.isNotEmpty()

        fun toMetadata(): EndpointMetadata = EndpointMetadata(
            parameterNames = parameterNames.toList(),
            inputHeaderNames = inputHeaderNames.toList(),
            outputHeaderNames = outputHeaderNames.toList()
        )
    }

    private class NameRegistry {
        private val fieldValues = HashMap<FieldRef, Value.NamedValue>()

        fun register(fieldRef: FieldRef, value: Value.NamedValue) {
            fieldValues[fieldRef] = value
        }

        fun lookup(fieldRef: FieldRef): Value.NamedValue? = fieldValues[fieldRef]

        fun entries(): Map<FieldRef, Value.NamedValue> = fieldValues.toMap()
    }

    private data class FieldRef(
        val ownerInternalName: String,
        val name: String,
        val descriptor: String
    )

    private enum class ValueKind {
        PARAMETER,
        HEADER,
        OTHER
    }

    private sealed interface Value {
        object Unknown : Value
        data class StringValue(val value: String) : Value
        data class NamedValue(val kind: ValueKind, val name: String?) : Value
    }

    private class InstructionInterpreter(
        private val ownerInternalName: String,
        private val registry: NameRegistry,
        private val collector: EndpointCollector?,
        private val externalRegistry: ExternalFieldRegistry,
        private val classNodeProvider: (String) -> ClassNode?,
        private val logger: Logger
    ) {
        private val stack = ArrayDeque<Value>()
        private val locals = HashMap<Int, Value>()

        fun process(method: MethodNode) {
            val instructions = method.instructions ?: return
            var current: AbstractInsnNode? = instructions.first
            while (current != null) {
                try {
                    handle(current)
                } catch (e: Exception) {
                    logger.debug(
                        "[tapik] Failed to interpret instruction ${method.name}#${current.opcode} in $ownerInternalName",
                        e
                    )
                }
                current = current.next
            }
        }

        private fun handle(node: AbstractInsnNode) {
            when (node) {
                is InsnNode -> handleInsn(node)
                is IntInsnNode -> handleIntInsn(node)
                is VarInsnNode -> handleVarInsn(node)
                is TypeInsnNode -> handleTypeInsn(node)
                is FieldInsnNode -> handleFieldInsn(node)
                is MethodInsnNode -> handleMethodInsn(node)
                is LdcInsnNode -> handleLdc(node)
                is InvokeDynamicInsnNode -> stack += Value.Unknown
                is MultiANewArrayInsnNode -> {
                    repeat(node.dims) { popNullable() }
                    stack += Value.Unknown
                }
                else -> Unit
            }
        }

        private fun handleInsn(node: InsnNode) {
            when (node.opcode) {
                Opcodes.ACONST_NULL -> stack += Value.Unknown
                Opcodes.ARETURN,
                Opcodes.ATHROW -> popNullable()
                Opcodes.POP -> popNullable()
                Opcodes.POP2 -> {
                    popNullable()
                    popNullable()
                }
                Opcodes.DUP -> stack += peek()
                Opcodes.DUP_X1 -> {
                    val v1 = pop()
                    val v2 = pop()
                    stack += v1
                    stack += v2
                    stack += v1
                }
                Opcodes.DUP_X2 -> {
                    val v1 = pop()
                    val v2 = pop()
                    val v3 = pop()
                    stack += v1
                    stack += v3
                    stack += v2
                    stack += v1
                }
                Opcodes.SWAP -> {
                    val v1 = pop()
                    val v2 = pop()
                    stack += v1
                    stack += v2
                }
                Opcodes.AASTORE -> {
                    popNullable()
                    popNullable()
                    popNullable()
                }
                Opcodes.AALOAD -> {
                    popNullable()
                    val array = pop()
                    stack += array
                }
                Opcodes.RETURN -> Unit
                else -> stack += Value.Unknown
            }
        }

        private fun handleIntInsn(node: IntInsnNode) {
            when (node.opcode) {
                Opcodes.BIPUSH,
                Opcodes.SIPUSH -> stack += Value.Unknown
                Opcodes.NEWARRAY -> {
                    popNullable()
                    stack += Value.Unknown
                }
            }
        }

        private fun handleVarInsn(node: VarInsnNode) {
            when (node.opcode) {
                Opcodes.ALOAD -> stack += locals[node.`var`] ?: Value.Unknown
                Opcodes.ILOAD,
                Opcodes.LLOAD,
                Opcodes.FLOAD,
                Opcodes.DLOAD -> stack += Value.Unknown
                Opcodes.ASTORE -> locals[node.`var`] = pop()
                Opcodes.ISTORE,
                Opcodes.LSTORE,
                Opcodes.FSTORE,
                Opcodes.DSTORE -> {
                    popNullable()
                    locals[node.`var`] = Value.Unknown
                }
            }
        }

        private fun handleTypeInsn(node: TypeInsnNode) {
            when (node.opcode) {
                Opcodes.NEW -> stack += Value.Unknown
                Opcodes.ANEWARRAY -> {
                    popNullable()
                    stack += Value.Unknown
                }
                Opcodes.CHECKCAST -> {
                    val value = pop()
                    stack += value
                }
                Opcodes.INSTANCEOF -> {
                    popNullable()
                    stack += Value.Unknown
                }
            }
        }

        private fun handleFieldInsn(node: FieldInsnNode) {
            val fieldRef = FieldRef(node.owner, node.name, node.desc)
            when (node.opcode) {
                Opcodes.GETSTATIC -> {
                    val named = registry.lookup(fieldRef) ?: externalRegistry.lookup(fieldRef)
                    stack += named ?: Value.Unknown
                }
                Opcodes.PUTSTATIC -> {
                    val value = pop()
                    val named = value as? Value.NamedValue
                    if (named != null && named.kind != ValueKind.OTHER) {
                        registry.register(fieldRef, named)
                    }
                }
                Opcodes.GETFIELD -> {
                    popNullable()
                    stack += Value.Unknown
                }
                Opcodes.PUTFIELD -> {
                    popNullable()
                    popNullable()
                }
            }
        }

        private fun handleMethodInsn(node: MethodInsnNode) {
            val argTypes = Type.getArgumentTypes(node.desc)
            val args = ArrayList<Value>(argTypes.size)
            repeat(argTypes.size) {
                args += pop()
            }
            args.reverse()
            val receiver = if (node.opcode != Opcodes.INVOKESTATIC) pop() else null

            val produced: Value? = when {
                node.owner.startsWith(QUERY_PARAMETER_COMPANION) -> buildQueryParameter(args)
                node.owner.startsWith(PATH_VARIABLE_COMPANION) -> buildPathVariable(args)
                node.owner.startsWith(HEADER_COMPANION) -> buildHeaderFromCompanion(node, args)
                node.owner == QUERY_PARAMETER_CLASS && (node.name == "optional" || node.name == "getOptional") ->
                    receiver.keepKind(ValueKind.PARAMETER)
                node.owner == HEADER_CLASS && node.name == "invoke" ->
                    receiver.keepKind(ValueKind.HEADER)
                node.owner == HEADER_INPUT_CLASS && node.name == "<init>" ->
                    buildHeaderFromConstructor(args)
                node.owner == HEADER_VALUES_CLASS && node.name == "<init>" ->
                    buildHeaderFromConstructor(args)
                node.owner == QUERY_PARAMETER_CLASS && node.name == "<init>" ->
                    buildQueryParameterFromConstructor(args)
                node.owner == PATH_VARIABLE_CLASS && node.name == "<init>" ->
                    buildPathVariableFromConstructor(args)
                node.owner == INPUT_HEADER_METHODS && node.name.startsWith("inputHeader") -> {
                    collector?.addInputHeader(args.lastOrNull().asNamed(ValueKind.HEADER)?.name)
                    Value.Unknown
                }
                node.owner == OUTPUT_HEADER_METHODS && node.name.startsWith("outputHeader") -> {
                    collector?.addOutputHeader(args.lastOrNull().asNamed(ValueKind.HEADER)?.name)
                    Value.Unknown
                }
                node.owner == QUERY_PARAMETER_METHODS && node.name.startsWith("addParameter") -> {
                    collector?.addParameter(args.lastOrNull().asNamed(ValueKind.PARAMETER)?.name)
                    Value.Unknown
                }
                node.owner == PATH_VARIABLE_METHODS && node.name.startsWith("addVariable") -> {
                    collector?.addParameter(args.lastOrNull().asNamed(ValueKind.PARAMETER)?.name)
                    Value.Unknown
                }
                else -> Value.Unknown
            }

            if (produced != null && !node.desc.endsWith(")V")) {
                stack += produced
            } else if (produced != null && node.desc.endsWith(")V")) {
                // void return already consumed
            } else if (!node.desc.endsWith(")V")) {
                stack += Value.Unknown
            }
        }

        private fun buildQueryParameter(args: List<Value>): Value =
            Value.NamedValue(ValueKind.PARAMETER, args.firstOrNull().asString())

        private fun buildPathVariable(args: List<Value>): Value =
            Value.NamedValue(ValueKind.PARAMETER, args.firstOrNull().asString())

        private fun buildHeaderFromCompanion(
            node: MethodInsnNode,
            args: List<Value>
        ): Value {
            val argName = args.firstOrNull().asString()
            val name = argName ?: resolveHeaderNameFromCompanion(node.owner, node.name)
            return Value.NamedValue(ValueKind.HEADER, name)
        }

        private fun resolveHeaderNameFromCompanion(owner: String, methodName: String): String? {
            val classNode = classNodeProvider(owner) ?: return null
            val method = classNode.methods.firstOrNull { it.name == methodName } ?: return null
            var current: AbstractInsnNode? = method.instructions?.first
            while (current != null) {
                when (current) {
                    is FieldInsnNode -> if (current.opcode == Opcodes.GETSTATIC) {
                        val fieldRef = FieldRef(current.owner, current.name, current.desc)
                        registry.lookup(fieldRef)?.name?.let { return it }
                        externalRegistry.lookup(fieldRef)?.name?.let { return it }
                        scanHeaderNameFromClinit(classNode, fieldRef.name)?.let { return it }
                    }
                    is LdcInsnNode -> if (current.cst is String) {
                        return current.cst as String
                    }
                }
                current = current.next
            }
            return null
        }

        private fun scanHeaderNameFromClinit(classNode: ClassNode, fieldName: String): String? {
            val clinit = classNode.methods.firstOrNull { it.name == CLINIT_METHOD_NAME } ?: return null
            var current: AbstractInsnNode? = clinit.instructions?.first
            var lastString: String? = null
            while (current != null) {
                when (current) {
                    is LdcInsnNode -> if (current.cst is String) {
                        lastString = current.cst as String
                    }
                    is FieldInsnNode -> if (
                        current.opcode == Opcodes.PUTSTATIC &&
                        current.owner == classNode.name &&
                        current.name == fieldName &&
                        current.desc == "Ldev/akif/tapik/http/Header;"
                    ) {
                        return lastString
                    }
                }
                current = current.next
            }
            return null
        }

        private fun buildHeaderFromConstructor(args: List<Value>): Value =
            Value.NamedValue(ValueKind.HEADER, args.firstOrNull().asString())

        private fun buildQueryParameterFromConstructor(args: List<Value>): Value =
            Value.NamedValue(ValueKind.PARAMETER, args.firstOrNull().asString())

        private fun buildPathVariableFromConstructor(args: List<Value>): Value =
            Value.NamedValue(ValueKind.PARAMETER, args.firstOrNull().asString())

        private fun Value?.keepKind(expected: ValueKind): Value =
            (this as? Value.NamedValue)?.takeIf { it.kind == expected } ?: Value.Unknown

        private fun Value?.asNamed(expected: ValueKind): Value.NamedValue? {
            val named = this as? Value.NamedValue ?: return null
            return if (named.kind == expected) named else null
        }

        private fun Value?.asString(): String? = (this as? Value.StringValue)?.value

        private fun handleLdc(node: LdcInsnNode) {
            val constant = node.cst
            stack += if (constant is String) {
                Value.StringValue(constant)
            } else {
                Value.Unknown
            }
        }

        private fun pop(): Value = stack.removeLastOrNull() ?: Value.Unknown

        private fun peek(): Value = stack.lastOrNull() ?: Value.Unknown

        private fun popNullable(): Value = if (stack.isEmpty()) Value.Unknown else stack.removeLast()

        companion object {
            private const val QUERY_PARAMETER_COMPANION = "dev/akif/tapik/http/QueryParameter\$Companion"
            private const val PATH_VARIABLE_COMPANION = "dev/akif/tapik/http/PathVariable\$Companion"
            private const val HEADER_COMPANION = "dev/akif/tapik/http/Header\$Companion"
            private const val QUERY_PARAMETER_CLASS = "dev/akif/tapik/http/QueryParameter"
            private const val PATH_VARIABLE_CLASS = "dev/akif/tapik/http/PathVariable"
            private const val HEADER_CLASS = "dev/akif/tapik/http/Header"
            private const val HEADER_INPUT_CLASS = "dev/akif/tapik/http/HeaderInput"
            private const val HEADER_VALUES_CLASS = "dev/akif/tapik/http/HeaderValues"
            private const val INPUT_HEADER_METHODS = "dev/akif/tapik/http/InputHeaderMethodsKt"
            private const val OUTPUT_HEADER_METHODS = "dev/akif/tapik/http/OutputHeaderMethodsKt"
            private const val QUERY_PARAMETER_METHODS = "dev/akif/tapik/http/QueryParameterMethodsKt"
            private const val PATH_VARIABLE_METHODS = "dev/akif/tapik/http/PathVariableMethodsKt"
        }
    }

    private inner class ExternalFieldRegistry {
        private val cache = HashMap<FieldRef, Value.NamedValue?>()
        private val processedOwners = HashSet<String>()

        fun lookup(fieldRef: FieldRef): Value.NamedValue? {
            cache[fieldRef]?.let { return it }
            if (processedOwners.add(fieldRef.ownerInternalName)) {
                load(fieldRef.ownerInternalName)
            }
            return cache[fieldRef]
        }

        private fun load(ownerInternalName: String) {
            val classNode = classNodeProvider(ownerInternalName) ?: return
            val localRegistry = NameRegistry()
            classNode.methods.firstOrNull { it.name == CLINIT_METHOD_NAME }
                ?.let { interpretMethod(ownerInternalName, it, localRegistry, null) }
            localRegistry.entries().forEach { (field, value) ->
                cache[field] = value
            }
        }
    }
}
