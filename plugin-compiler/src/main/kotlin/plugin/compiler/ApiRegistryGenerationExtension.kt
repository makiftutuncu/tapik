package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import java.nio.file.Path

internal class ApiRegistryGenerationExtension(
    private val outputDirectory: Path,
    private val messages: MessageCollector
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) {
        val apiClass = pluginContext.referenceClass(API_CLASS_ID)?.owner
        val endpointClass = pluginContext.referenceClass(ENDPOINT_CLASS_ID)?.owner
        if (apiClass == null || endpointClass == null) {
            RegistryClassWriter.synchronize(outputDirectory, emptyList())
            return
        }
        val apiTypes = mutableListOf<IrClass>()
        moduleFragment.acceptChildrenVoid(
            object : IrVisitorVoid() {
                override fun visitElement(element: IrElement) {
                    element.acceptChildrenVoid(this)
                }

                override fun visitClass(declaration: IrClass) {
                    if (declaration.isConcreteApi(apiClass)) {
                        apiTypes += declaration
                    }
                    super.visitClass(declaration)
                }
            }
        )
        if (!validateEndpointProperties(apiTypes, apiClass, endpointClass)) {
            RegistryClassWriter.synchronize(outputDirectory, emptyList())
            return
        }
        val registeredTypes = apiTypes.mapNotNull { apiType -> apiType.registeredApiType() }
        if (registeredTypes.size != apiTypes.size) {
            RegistryClassWriter.synchronize(outputDirectory, emptyList())
            return
        }

        RegistryClassWriter.synchronize(outputDirectory, registeredTypes)
    }

    private fun validateEndpointProperties(
        apiTypes: List<IrClass>,
        apiClass: IrClass,
        endpointClass: IrClass
    ): Boolean {
        var valid = true
        val inspected = mutableSetOf<IrProperty>()
        apiTypes.forEach { apiType ->
            apiType.apiHierarchy(apiClass).forEach { declaringType ->
                declaringType.declarations
                    .filterIsInstance<IrProperty>()
                    .filter { property -> property.getter?.returnType?.classOrNull?.owner == endpointClass }
                    .filter(inspected::add)
                    .filter { property -> property.visibility != DescriptorVisibilities.PUBLIC }
                    .forEach { property ->
                        val owner = declaringType.fqNameWhenAvailable?.asString() ?: declaringType.name.asString()
                        messages.report(
                            ERROR,
                            "Tapik endpoint property '$owner.${property.name}' must be public for generated targets"
                        )
                        valid = false
                    }
            }
        }
        return valid
    }

    private fun IrClass.registeredApiType(): RegisteredApiType? {
        val displayName = fqNameWhenAvailable?.asString() ?: name.asString()
        if (!isEffectivelyPublic()) {
            messages.report(ERROR, "Tapik API type '$displayName' must be public")
            return null
        }
        return when (kind) {
            ClassKind.OBJECT -> RegisteredApiType(jvmInternalName(), ApiInstantiation.OBJECT)
            ClassKind.CLASS -> {
                val constructor = constructors.singleOrNull { it.parameters.isEmpty() }
                if (constructor == null || constructor.visibility != DescriptorVisibilities.PUBLIC) {
                    messages.report(
                        ERROR,
                        "Tapik API class '$displayName' must declare a public no-argument constructor"
                    )
                    null
                } else {
                    RegisteredApiType(jvmInternalName(), ApiInstantiation.CONSTRUCTOR)
                }
            }
            else -> null
        }
    }
}

private fun IrClass.isConcreteApi(apiClass: IrClass): Boolean =
    !name.isSpecial &&
        kind in setOf(ClassKind.CLASS, ClassKind.OBJECT) &&
        modality != Modality.ABSTRACT &&
        isSubclassOf(apiClass)

private fun IrClass.isEffectivelyPublic(): Boolean {
    var declaration: IrClass? = this
    while (declaration != null) {
        if (declaration.visibility != DescriptorVisibilities.PUBLIC) return false
        declaration = declaration.parent as? IrClass
    }
    return true
}

private fun IrClass.apiHierarchy(apiClass: IrClass): Sequence<IrClass> =
    sequence {
        val pending = ArrayDeque<IrClass>()
        val visited = mutableSetOf<IrClass>()
        pending += this@apiHierarchy
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            if (!visited.add(current) || current == apiClass) continue
            yield(current)
            current.superTypes
                .mapNotNull { type -> type.classOrNull?.owner }
                .filter { parent -> parent == apiClass || parent.isSubclassOf(apiClass) }
                .forEach(pending::addLast)
        }
    }

private fun IrClass.jvmInternalName(): String {
    val classNames = mutableListOf<String>()
    var declaration = this
    while (true) {
        classNames += declaration.name.asString()
        val parent = declaration.parent
        if (parent is IrClass) {
            declaration = parent
        } else {
            val file = requireNotNull(parent as? IrFile) { "API object must be declared in a Kotlin file" }
            val packagePath = file.packageFqName.asString().replace('.', '/')
            val classPath = classNames.asReversed().joinToString("$")
            return if (packagePath.isEmpty()) classPath else "$packagePath/$classPath"
        }
    }
}

private val API_CLASS_ID: ClassId = ClassId.topLevel(FqName("dev.akif.tapik.Api"))
private val ENDPOINT_CLASS_ID: ClassId = ClassId.topLevel(FqName("dev.akif.tapik.Endpoint"))
