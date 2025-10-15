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

internal data class ParameterMetadata(
    val name: String?,
    val isRequired: Boolean?,
    val hasDefault: Boolean?
)

internal data class HeaderMetadata(
    val name: String?,
    val hasKnownValues: Boolean
)

internal data class EndpointMetadata(
    val parameters: List<ParameterMetadata>,
    val inputHeaders: List<HeaderMetadata>,
    val outputHeaders: List<HeaderMetadata>
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
        private val parameters = ArrayList<ParameterMetadata>()
        private val inputHeaders = ArrayList<HeaderMetadata>()
        private val outputHeaders = ArrayList<HeaderMetadata>()

        fun addParameter(parameter: Value.NamedValue?) {
            parameters += ParameterMetadata(
                name = parameter?.name,
                isRequired = parameter?.isRequired ?: true,
                hasDefault = parameter?.hasDefault ?: false
            )
        }

        fun addParametersIfEmpty(values: List<Value.NamedValue?>) {
            if (values.isEmpty() || parameters.isNotEmpty()) {
                return
            }
            values.forEach { addParameter(it) }
        }

        fun addInputHeader(header: Value.NamedValue?) {
            inputHeaders += HeaderMetadata(
                name = header?.name,
                hasKnownValues = header?.hasKnownValues == true
            )
        }

        fun addInputHeadersIfEmpty(values: List<Value.NamedValue?>) {
            if (values.isEmpty() || inputHeaders.isNotEmpty()) {
                return
            }
            values.forEach { addInputHeader(it) }
        }

        fun addOutputHeader(header: Value.NamedValue?) {
            outputHeaders += HeaderMetadata(
                name = header?.name,
                hasKnownValues = header?.hasKnownValues == true
            )
        }

        fun addOutputHeadersIfEmpty(values: List<Value.NamedValue?>) {
            if (values.isEmpty() || outputHeaders.isNotEmpty()) {
                return
            }
            values.forEach { addOutputHeader(it) }
        }

        fun isNotEmpty(): Boolean =
            parameters.isNotEmpty() ||
                inputHeaders.isNotEmpty() ||
                outputHeaders.isNotEmpty()

        fun toMetadata(): EndpointMetadata = EndpointMetadata(
            parameters = parameters.toList(),
            inputHeaders = inputHeaders.toList(),
            outputHeaders = outputHeaders.toList()
        )
    }

    private class NameRegistry {
        private val fieldValues = HashMap<FieldRef, Value>()

        fun register(fieldRef: FieldRef, value: Value) {
            fieldValues[fieldRef] = value
        }

        fun lookup(fieldRef: FieldRef): Value? = fieldValues[fieldRef]

        fun entries(): Map<FieldRef, Value> = fieldValues.toMap()
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
        object NullValue : Value
        data class BooleanValue(val value: Boolean) : Value
        data class StringValue(val value: String) : Value
        data class NamedValue(
            val kind: ValueKind,
            val name: String?,
            val hasKnownValues: Boolean = false,
            val isRequired: Boolean? = null,
            val hasDefault: Boolean? = null
        ) : Value
        data class NamedGroup(
            val kind: ValueKind,
            val values: List<NamedValue?>
        ) : Value
        data class EndpointValue(
            val parameters: List<NamedValue?>,
            val inputHeaders: List<NamedValue?>,
            val outputHeaders: List<NamedValue?>
        ) : Value
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
                Opcodes.ACONST_NULL -> stack += Value.NullValue
                Opcodes.ICONST_0 -> stack += Value.BooleanValue(false)
                Opcodes.ICONST_1 -> stack += Value.BooleanValue(true)
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
                    val stored = registry.lookup(fieldRef) ?: externalRegistry.lookup(fieldRef)
                    when (stored) {
                        is Value.EndpointValue -> {
                            collector?.addParametersIfEmpty(stored.parameters)
                            collector?.addInputHeadersIfEmpty(stored.inputHeaders)
                            collector?.addOutputHeadersIfEmpty(stored.outputHeaders)
                            stack += stored
                        }
                        null -> stack += Value.Unknown
                        else -> stack += stored
                    }
                }
                Opcodes.PUTSTATIC -> {
                    val value = pop()
                    when (value) {
                        is Value.NamedValue -> if (value.kind != ValueKind.OTHER) {
                            registry.register(fieldRef, value)
                        }
                        is Value.NamedGroup -> registry.register(fieldRef, value)
                        is Value.EndpointValue -> registry.register(fieldRef, value)
                        else -> Unit
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
            val rawArgs = ArrayList<Value>(argTypes.size)
            repeat(argTypes.size) {
                rawArgs += pop()
            }
            val args = rawArgs.asReversed()
            val receiver = if (node.opcode != Opcodes.INVOKESTATIC) pop() else null

            val produced: Value? = when {
                node.owner.startsWith(TUPLE_CLASS_PREFIX) && node.name == "<init>" ->
                    buildTupleFromConstructor(args)
                node.owner == HTTP_ENDPOINT_CLASS && node.name == "<init>" ->
                    applyHttpEndpointMetadata(argTypes, args)
                node.owner.startsWith(QUERY_PARAMETER_COMPANION) -> buildQueryParameter(args)
                node.owner.startsWith(PATH_VARIABLE_COMPANION) -> buildPathVariable(args)
                node.owner.startsWith(HEADER_COMPANION) -> buildHeaderFromCompanion(node, args)
                node.owner == QUERY_PARAMETER_CLASS && node.name == "optional" ->
                    receiver.keepKind(ValueKind.PARAMETER) { value ->
                        value.copy(isRequired = false, hasDefault = true)
                    }
                node.owner == QUERY_PARAMETER_CLASS && node.name == "getOptional" ->
                    receiver.keepKind(ValueKind.PARAMETER) { value ->
                        value.copy(isRequired = false, hasDefault = false)
                    }
                node.owner == HEADER_CLASS && node.name == "invoke" ->
                    receiver.keepKind(ValueKind.HEADER) { value ->
                        value.copy(hasKnownValues = true)
                    }
                node.owner == HEADER_INPUT_CLASS && node.name == "<init>" ->
                    buildHeaderFromConstructor(args, hasKnownValues = false)
                node.owner == HEADER_VALUES_CLASS && node.name == "<init>" ->
                    buildHeaderFromConstructor(args, hasKnownValues = true)
                node.owner == QUERY_PARAMETER_CLASS && node.name == "<init>" ->
                    buildQueryParameterFromConstructor(args)
                node.owner == PATH_VARIABLE_CLASS && node.name == "<init>" ->
                    buildPathVariableFromConstructor(args)
                node.owner == INPUT_HEADER_METHODS && node.name.startsWith("inputHeader") -> {
                    collector?.addInputHeader(args.lastOrNull().asNamed(ValueKind.HEADER))
                    Value.Unknown
                }
                node.owner == OUTPUT_HEADER_METHODS && node.name.startsWith("outputHeader") -> {
                    collector?.addOutputHeader(args.lastOrNull().asNamed(ValueKind.HEADER))
                    Value.Unknown
                }
                node.owner == QUERY_PARAMETER_METHODS && node.name.startsWith("addParameter") -> {
                    collector?.addParameter(args.lastOrNull().asNamed(ValueKind.PARAMETER))
                    Value.Unknown
                }
                node.owner == PATH_VARIABLE_METHODS && node.name.startsWith("addVariable") -> {
                    collector?.addParameter(args.lastOrNull().asNamed(ValueKind.PARAMETER))
                    Value.Unknown
                }
                else -> Value.Unknown
            }

            if (produced != null && !node.desc.endsWith(")V")) {
                stack += produced
            } else if (produced != null && node.desc.endsWith(")V")) {
                if (stack.isNotEmpty()) {
                    stack[stack.lastIndex] = produced
                }
            } else if (!node.desc.endsWith(")V")) {
                stack += Value.Unknown
            }
        }

        private fun buildQueryParameter(args: List<Value>): Value =
            Value.NamedValue(
                kind = ValueKind.PARAMETER,
                name = args.firstOrNull().asString(),
                isRequired = true,
                hasDefault = false
            )

        private fun buildPathVariable(args: List<Value>): Value =
            Value.NamedValue(
                kind = ValueKind.PARAMETER,
                name = args.firstOrNull().asString(),
                isRequired = true,
                hasDefault = false
            )

        private fun buildHeaderFromCompanion(
            node: MethodInsnNode,
            args: List<Value>
        ): Value {
            val providedName = args.firstOrNull().asString()
            val resolved = resolveHeaderFromCompanion(node.owner, node.name)
            return when {
                resolved != null && providedName != null -> resolved.copy(name = providedName)
                resolved != null -> resolved
                providedName != null -> Value.NamedValue(ValueKind.HEADER, providedName)
                else -> Value.NamedValue(ValueKind.HEADER, null)
            }
        }

        private fun resolveHeaderFromCompanion(owner: String, methodName: String): Value.NamedValue? {
            val classNode = classNodeProvider(owner) ?: return null
            val method = classNode.methods.firstOrNull { it.name == methodName } ?: return null
            var current: AbstractInsnNode? = method.instructions?.first
            while (current != null) {
                when (current) {
                    is FieldInsnNode -> if (current.opcode == Opcodes.GETSTATIC) {
                        val fieldRef = FieldRef(current.owner, current.name, current.desc)
                        registry.lookup(fieldRef).asNamed(ValueKind.HEADER)?.let { return it }
                        externalRegistry.lookup(fieldRef).asNamed(ValueKind.HEADER)?.let { return it }
                        scanHeaderFromClinit(classNode, fieldRef.name)?.let { return it }
                    }
                    is LdcInsnNode -> if (current.cst is String) {
                        return Value.NamedValue(ValueKind.HEADER, current.cst as String)
                    }
                }
                current = current.next
            }
            return null
        }

        private fun scanHeaderFromClinit(classNode: ClassNode, fieldName: String): Value.NamedValue? {
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
                        return Value.NamedValue(ValueKind.HEADER, lastString)
                    }
                }
                current = current.next
            }
            return null
        }

        private fun buildHeaderFromConstructor(args: List<Value>, hasKnownValues: Boolean): Value =
            Value.NamedValue(ValueKind.HEADER, args.firstOrNull().asString(), hasKnownValues)

        private fun buildQueryParameterFromConstructor(args: List<Value>): Value {
            val name = args.firstOrNull().asString()
            val required = args.getOrNull(2).asBoolean()
            val hasDefault: Boolean? = when (val defaultArg = args.getOrNull(3)) {
                null -> false
                is Value.NullValue -> false
                is Value.Unknown -> null
                else -> true
            }
            return Value.NamedValue(
                kind = ValueKind.PARAMETER,
                name = name,
                isRequired = required ?: true,
                hasDefault = hasDefault
            )
        }

        private fun buildPathVariableFromConstructor(args: List<Value>): Value =
            Value.NamedValue(
                kind = ValueKind.PARAMETER,
                name = args.firstOrNull().asString(),
                isRequired = true,
                hasDefault = false
            )

        private fun buildTupleFromConstructor(args: List<Value>): Value {
            val parameterValues = args.map { it.asNamed(ValueKind.PARAMETER) }
            if (parameterValues.any { it != null }) {
                return Value.NamedGroup(ValueKind.PARAMETER, parameterValues)
            }
            val headerValues = args.map { it.asNamed(ValueKind.HEADER) }
            if (headerValues.any { it != null }) {
                return Value.NamedGroup(ValueKind.HEADER, headerValues)
            }
            return Value.Unknown
        }

        private fun applyHttpEndpointMetadata(argTypes: Array<Type>, args: List<Value>): Value {
            var parameterGroup: Value.NamedGroup? = null
            var inputHeaderGroup: Value.NamedGroup? = null
            var outputHeaderGroup: Value.NamedGroup? = null
            val bodyIndex = argTypes.indexOfFirst { it.className == HTTP_BODY_CLASS }

            argTypes.zip(args).forEachIndexed { index, (_, value) ->
                if (parameterGroup == null) {
                    parameterGroup = value.asGroup(ValueKind.PARAMETER) ?: parameterGroup
                }
                val headerGroup = value.asGroup(ValueKind.HEADER)
                if (headerGroup != null) {
                    if (bodyIndex >= 0 && index < bodyIndex && inputHeaderGroup == null) {
                        inputHeaderGroup = headerGroup
                    } else {
                        outputHeaderGroup = headerGroup
                    }
                }
            }

            parameterGroup?.let { group ->
                collector?.addParametersIfEmpty(group.values)
            }
            inputHeaderGroup?.let { group ->
                collector?.addInputHeadersIfEmpty(group.values)
            }
            outputHeaderGroup?.let { group ->
                collector?.addOutputHeadersIfEmpty(group.values)
            }

            return Value.EndpointValue(
                parameters = parameterGroup?.values.orEmpty(),
                inputHeaders = inputHeaderGroup?.values.orEmpty(),
                outputHeaders = outputHeaderGroup?.values.orEmpty()
            )
        }

        private fun Value?.keepKind(
            expected: ValueKind,
            transform: (Value.NamedValue) -> Value = { it }
        ): Value =
            (this as? Value.NamedValue)?.takeIf { it.kind == expected }?.let(transform) ?: Value.Unknown

        private fun Value?.asNamed(expected: ValueKind): Value.NamedValue? {
            val named = this as? Value.NamedValue ?: return null
            return if (named.kind == expected) named else null
        }

        private fun Value?.asGroup(expected: ValueKind): Value.NamedGroup? {
            val group = this as? Value.NamedGroup ?: return null
            return if (group.kind == expected) group else null
        }

        private fun Value?.asString(): String? = (this as? Value.StringValue)?.value

        private fun Value?.asBoolean(): Boolean? = (this as? Value.BooleanValue)?.value

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
            private const val HTTP_ENDPOINT_CLASS = "dev/akif/tapik/http/HttpEndpoint"
            private const val TUPLE_CLASS_PREFIX = "dev/akif/tapik/tuples/Tuple"
            private const val HTTP_BODY_CLASS = "dev.akif.tapik.http.Body"
        }
    }

    private inner class ExternalFieldRegistry {
        private val cache = HashMap<FieldRef, Value>()
        private val processedOwners = HashSet<String>()

        fun lookup(fieldRef: FieldRef): Value? {
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
