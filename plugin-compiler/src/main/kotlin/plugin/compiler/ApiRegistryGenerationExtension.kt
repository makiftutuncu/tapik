@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isSubclassOf
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
        val sourceFile = moduleFragment.files.firstOrNull()
        val declarations = sourceFile?.let(pluginContext::finderForSource)
        val apiClass = declarations?.findClass(API_CLASS_ID)?.owner
        val endpointClass = declarations?.findClass(ENDPOINT_CLASS_ID)?.owner
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
        val registeredTypes = apiTypes.mapNotNull { apiType -> apiType.registeredApiType(messages) }
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

private val API_CLASS_ID: ClassId = ClassId.topLevel(FqName("dev.akif.tapik.Api"))
private val ENDPOINT_CLASS_ID: ClassId = ClassId.topLevel(FqName("dev.akif.tapik.Endpoint"))
