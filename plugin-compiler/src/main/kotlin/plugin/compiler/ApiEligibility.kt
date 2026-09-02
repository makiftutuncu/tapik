@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isSubclassOf

internal fun IrClass.isConcreteApi(apiClass: IrClass): Boolean =
    !name.isSpecial &&
        kind in setOf(ClassKind.CLASS, ClassKind.OBJECT) &&
        modality != Modality.ABSTRACT &&
        isSubclassOf(apiClass)

internal fun IrClass.registeredApiType(messages: MessageCollector): RegisteredApiType? {
    val displayName = fqNameWhenAvailable?.asString() ?: name.asString()
    if (!isEffectivelyPublic()) {
        messages.report(ERROR, "tapik API type '$displayName' must be public")
        return null
    }
    return when (kind) {
        ClassKind.OBJECT -> RegisteredApiType(jvmInternalName(), ApiInstantiation.OBJECT)
        ClassKind.CLASS -> registeredApiClass(messages, displayName)
        else -> null
    }
}

private fun IrClass.registeredApiClass(
    messages: MessageCollector,
    displayName: String
): RegisteredApiType? {
    if (isInner) {
        messages.report(ERROR, "tapik API class '$displayName' must not be inner")
        return null
    }
    if (typeParameters.isNotEmpty()) {
        messages.report(ERROR, "tapik API class '$displayName' must not declare type parameters")
        return null
    }
    val constructor =
        constructors.singleOrNull { candidate ->
            candidate.visibility == DescriptorVisibilities.PUBLIC &&
                (candidate.parameters.isEmpty() || candidate.parameters.all { it.defaultValue != null })
        }
    if (constructor == null) {
        messages.report(
            ERROR,
            "tapik API class '$displayName' must declare a public no-argument constructor"
        )
        return null
    }
    return RegisteredApiType(jvmInternalName(), ApiInstantiation.CONSTRUCTOR)
}

private fun IrClass.isEffectivelyPublic(): Boolean {
    var declaration: IrClass? = this
    while (declaration != null) {
        if (declaration.visibility != DescriptorVisibilities.PUBLIC) return false
        declaration = declaration.parent as? IrClass
    }
    return true
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
            val file = requireNotNull(parent as? IrFile) { "API type must be declared in a Kotlin file" }
            val packagePath = file.packageFqName.asString().replace('.', '/')
            val classPath = classNames.asReversed().joinToString("$")
            return if (packagePath.isEmpty()) classPath else "$packagePath/$classPath"
        }
    }
}
