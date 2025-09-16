package dev.akif.tapik.gradle

import org.objectweb.asm.*
import java.io.File
import java.io.InputStream
import java.util.Locale.getDefault

data class ResolvedEndpointInfo(
    val name: String,
    val packageName: String,
    val fullType: String,
    val file: String,
    val isDelegated: Boolean
)

class BytecodeTypeAnalyzer {
    fun analyzeEndpoints(
        endpointPackages: List<String>,
        compiledClassesDir: File,
        logger: org.gradle.api.logging.Logger
    ): List<ResolvedEndpointInfo> {
        val endpoints = mutableListOf<ResolvedEndpointInfo>()

        try {
            // Find all compiled class files
            val classFiles = compiledClassesDir.walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .toList()

            logger.debug("Found ${classFiles.size} compiled class files to analyze")

            classFiles.forEach { classFile ->
                try {
                    val classInfo = analyzeClassFile(classFile, logger)
                    if (classInfo != null) {
                        val packageName = classInfo.packageName
                        val inScope = endpointPackages.isEmpty() || endpointPackages.any { packageName.startsWith(it) }

                        if (inScope) {
                            endpoints.addAll(classInfo.endpoints)
                        }
                    }
                } catch (e: Exception) {
                    logger.debug("Failed to analyze class ${classFile.name}", e)
                }
            }

        } catch (e: Exception) {
            logger.error("Bytecode analysis failed: ${e.message}", e)
            throw e
        }

        return endpoints
    }

    private fun analyzeClassFile(classFile: File, logger: org.gradle.api.logging.Logger): ClassInfo? {
        return try {
            val inputStream: InputStream = classFile.inputStream()
            val classReader = ClassReader(inputStream)
            val classInfo = ClassInfo()

            classReader.accept(object : ClassVisitor(Opcodes.ASM9) {
                override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<String>?) {
                    classInfo.packageName = extractPackageName(name)
                }

                override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?): MethodVisitor? {
                    logger.debug("Analyzing method: $name")

                    // Skip synthetic methods entirely - be more comprehensive
                    if (name.contains("\$") || name.startsWith("_") || name.contains("delegate") || name.contains("lambda") ||
                        name.contains("$") || name.contains("_delegate") || name.contains("_lambda")) {
                        logger.debug("Skipping synthetic method: $name")
                        return null
                    }

                    // Look for getter methods that return HttpEndpoint directly (not collections)
                    if (name.startsWith("get") && isDirectHttpEndpointMethod(descriptor, signature)) {
                        val propertyName = name.substring(3).replaceFirstChar { it.lowercase(getDefault()) }

                        // Double-check the property name for synthetic patterns
                        if (propertyName.contains("\$") || propertyName.startsWith("_") || propertyName.contains("delegate") ||
                            propertyName.contains("lambda") || propertyName.contains("$") || propertyName.contains("_delegate") ||
                            propertyName.contains("_lambda")) {
                            logger.debug("Skipping synthetic property: $propertyName")
                            return null
                        }

                        logger.debug("Found HttpEndpoint method: $name -> $propertyName")
                        logger.debug("  Descriptor: $descriptor")
                        logger.debug("  Signature: $signature")

                        val fullType = if (signature != null) {
                            parseGenericSignature(signature)
                        } else {
                            parseDescriptor(descriptor)
                        }

                        logger.debug("  Parsed type: $fullType")

                        classInfo.endpoints.add(ResolvedEndpointInfo(
                            name = propertyName,
                            packageName = classInfo.packageName,
                            fullType = fullType,
                            file = classFile.name,
                            isDelegated = true // These are getter methods for delegated properties
                        ))
                    }
                    return null
                }
            }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

            if (classInfo.endpoints.isNotEmpty()) classInfo else null
        } catch (e: Exception) {
            logger.debug("Failed to analyze class file ${classFile.name}: ${e.message}")
            null
        }
    }

    private fun extractPackageName(className: String): String {
        val lastSlash = className.lastIndexOf('/')
        if (lastSlash > 0) {
            return className.take(lastSlash).replace('/', '.')
        }
        return ""
    }

    private fun isDirectHttpEndpointMethod(descriptor: String, signature: String?): Boolean {
        // Check if this method returns HttpEndpoint directly (not a collection or wrapper)
        val hasHttpEndpoint = descriptor.contains("Ldev/akif/tapik/http/HttpEndpoint") ||
                (signature != null && signature.contains("Ldev/akif/tapik/http/HttpEndpoint"))

        if (!hasHttpEndpoint) return false

        // Ensure it's not a collection type (List, Set, etc.)
        val isCollection = descriptor.contains("Ljava/util/") ||
                          descriptor.contains("Ljava/lang/Iterable") ||
                          (signature != null && (signature.contains("Ljava/util/") || signature.contains("Ljava/lang/Iterable")))

        return !isCollection
    }

    private fun parseGenericSignature(signature: String): String {
        // Find the HttpEndpoint type in the signature
        val httpEndpointIndex = signature.indexOf("Ldev/akif/tapik/http/HttpEndpoint")
        if (httpEndpointIndex >= 0) {
            // Extract the HttpEndpoint part and parse it more carefully
            val httpEndpointPart = signature.substring(httpEndpointIndex)
            val endIndex = findMatchingBracket(httpEndpointPart, 0)
            if (endIndex > 0) {
                val httpEndpointSignature = httpEndpointPart.take(endIndex + 1)
                return parseHttpEndpointSignature(httpEndpointSignature)
            }
        }

        // Fallback to simple parsing with tapik type simplification
        val result = signature.replace('/', '.').replace('L', ' ').trim()
        return simplifyTapikTypes(result)
    }

    private fun findMatchingBracket(signature: String, startIndex: Int): Int {
        var depth = 0
        for (i in startIndex until signature.length) {
            when (signature[i]) {
                '<' -> depth++
                '>' -> {
                    depth--
                    if (depth == 0) return i
                }
            }
        }
        return -1
    }

    private fun parseHttpEndpointSignature(signature: String): String {
        val result = StringBuilder()
        result.append("HttpEndpoint") // Use simple name instead of fully qualified

        // Extract type parameters - HttpEndpoint has exactly 5 type parameters
        val typeParams = mutableListOf<String>()
        var current = signature.indexOf('<')
        if (current >= 0) {
            current++ // Skip '<'
            var depth = 1
            var start = current
            var i = current

            while (i < signature.length && depth > 0) {
                when (signature[i]) {
                    '<' -> depth++
                    '>' -> depth--
                    ';' -> if (depth == 1) {
                        typeParams.add(parseTypeParameter(signature.substring(start, i)))
                        start = i + 1
                    }
                }
                i++
            }

            if (start < i - 1) {
                typeParams.add(parseTypeParameter(signature.substring(start, i - 1)))
            }
        }

        if (typeParams.isNotEmpty()) {
            result.append("<")
            result.append(typeParams.joinToString(", "))
            result.append(">")
        }

        return result.toString()
    }

    private fun parseTypeParameter(param: String): String {
        return when {
            param.startsWith("L") -> {
                // Handle complex generic types like Ldev/akif/tapik/tuples/Tuple1<Ldev/akif/tapik/http/Header<*>;Ldev/akif/tapik/http/Header<Ldev/akif/tapik/http/MediaType;>;>;
                val className = param.substring(1).replace('/', '.')
                parseComplexGenericType(className)
            }
            param.startsWith("T") -> "?" // Wildcard
            else -> param
        }
    }

    private fun parseComplexGenericType(typeString: String): String {
        // This method handles complex generic types and converts them to the proper format
        return when {
            // Handle Tuple types with generics
            typeString.startsWith("dev.akif.tapik.tuples.Tuple") -> {
                val tupleMatch = Regex("Tuple(\\d+)").find(typeString)
                val tupleCount = tupleMatch?.groupValues?.get(1)?.toInt() ?: 0

                when {
                    // Header tuples -> HeadersN
                    typeString.contains("dev.akif.tapik.http.Header") -> {
                        if (tupleCount == 0) {
                            "Headers0"
                        } else {
                            // Extract only the non-wildcard header types
                            val headerTypes = extractHeaderTypes(typeString)
                            if (headerTypes.isEmpty()) "Headers$tupleCount" else "Headers$tupleCount<${headerTypes.joinToString(", ")}>"
                        }
                    }
                    // OutputBody tuples -> OutputBodiesN
                    typeString.contains("dev.akif.tapik.http.OutputBody") -> {
                        if (tupleCount == 0) {
                            "OutputBodies0"
                        } else {
                            // Extract only the non-wildcard output body types
                            val outputBodyTypes = extractOutputBodyTypes(typeString)
                            if (outputBodyTypes.isEmpty()) "OutputBodies$tupleCount" else "OutputBodies$tupleCount<${outputBodyTypes.joinToString(", ")}>"
                        }
                    }
                    // Parameter tuples -> ParametersN
                    typeString.contains("dev.akif.tapik.http.Parameter") -> {
                        if (tupleCount == 0) {
                            "Parameters0"
                        } else {
                            // Extract parameter types
                            val parameterTypes = extractParameterTypes(typeString)
                            if (parameterTypes.isEmpty()) "Parameters$tupleCount" else "Parameters$tupleCount<${parameterTypes.joinToString(", ")}>"
                        }
                    }
                    else -> "Tuple$tupleCount"
                }
            }
            // Handle simple types
            typeString.startsWith("dev.akif.tapik.http.") -> {
                parseTapikType(typeString)
            }
            // Handle non-tapik types - keep fully qualified
            else -> typeString.substringAfterLast('.')
        }
    }

    private fun extractHeaderTypes(typeString: String): List<String> {
        // For HeadersN<H1, H2, ...> = TupleN<Header<*>, Header<H1>, Header<H2>, ...>
        // We want to extract only H1, H2, ... (skip the first Header<*>)
        val headerPattern = Regex("Header<([^>]+)>")
        val matches = headerPattern.findAll(typeString).toList()

        return matches.drop(1).map { match ->
            val headerType = match.groupValues[1]
            if (headerType == "*") "?" else cleanTypeName(headerType)
        }
    }

    private fun extractOutputBodyTypes(typeString: String): List<String> {
        // For OutputBodiesN<B1, B2, ...> = TupleN<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, ...>
        // We want to extract only B1, B2, ... (skip the first OutputBody<*>)
        val outputBodyPattern = Regex("OutputBody<([^>]+)>")
        val matches = outputBodyPattern.findAll(typeString).toList()

        return matches.drop(1).map { match ->
            val bodyType = match.groupValues[1]
            if (bodyType == "*") "?" else {
                val cleanedType = cleanTypeName(bodyType)
                // Wrap in JsonBody if it's not already a body type (StringBody, RawBody, EmptyBody)
                when (cleanedType) {
                    "StringBody", "RawBody", "EmptyBody" -> cleanedType
                    else -> "JsonBody<$cleanedType>"
                }
            }
        }
    }

    private fun extractParameterTypes(typeString: String): List<String> {
        // For ParametersN<P1, P2, ...> = TupleN<Parameter<*>, Parameter<P1>, Parameter<P2>, ...>
        // We want to extract only P1, P2, ... (skip the first Parameter<*>)
        val parameterPattern = Regex("Parameter<([^>]+)>")
        val matches = parameterPattern.findAll(typeString).toList()

        return matches.drop(1).map { match ->
            val parameterType = match.groupValues[1]
            if (parameterType == "*") "?" else cleanTypeName(parameterType)
        }
    }

    private fun cleanTypeName(typeName: String): String {
        // Remove bytecode artifacts and convert to clean type names
        return typeName
            .replace("Ldev/akif/tapik/http/", "")
            .replace("Ldev/akif/app/", "")
            .replace('/', '.')
            .replace(";", "")
            .trim()
            .let { cleaned ->
                when {
                    // Handle tapik body types
                    cleaned.startsWith("dev.akif.tapik.http.") -> {
                        val simpleName = cleaned.substringAfterLast('.')
                        when (simpleName) {
                            "StringBody" -> "StringBody"
                            "RawBody" -> "RawBody"
                            "EmptyBody" -> "EmptyBody"
                            else -> simpleName
                        }
                    }
                    // Handle app types
                    cleaned.startsWith("dev.akif.app.") -> cleaned.substringAfterLast('.')
                    // Handle other types
                    cleaned.contains('.') -> cleaned.substringAfterLast('.')
                    else -> cleaned
                }
            }
    }

    private fun extractGenericTypes(typeString: String): List<String> {
        // Extract generic type parameters from complex types
        val genericTypes = mutableListOf<String>()
        val startIndex = typeString.indexOf('<')
        if (startIndex == -1) return genericTypes

        var depth = 0
        var start = startIndex + 1
        var i = startIndex + 1

        while (i < typeString.length) {
            when (typeString[i]) {
                '<' -> depth++
                '>' -> depth--
                ';' -> if (depth == 0) {
                    val genericType = typeString.substring(start, i)
                    genericTypes.add(parseSimpleType(genericType))
                    start = i + 1
                }
            }
            i++
        }

        if (start < typeString.length - 1) {
            val genericType = typeString.substring(start, typeString.length - 1)
            genericTypes.add(parseSimpleType(genericType))
        }

        return genericTypes
    }

    private fun parseSimpleType(typeString: String): String {
        return when {
            typeString.startsWith("L") -> {
                val className = typeString.substring(1).replace('/', '.')
                when {
                    className.startsWith("dev.akif.tapik.") -> className.substringAfterLast('.')
                    else -> className.substringAfterLast('.')
                }
            }
            typeString.startsWith("T") -> "?"
            else -> typeString
        }
    }

    private fun parseTapikType(className: String): String {
        return when {
            // Parameters types - Tuple0, Tuple1, Tuple2, etc.
            className.startsWith("dev.akif.tapik.tuples.Tuple") && !className.contains("Header<") && !className.contains("OutputBody<") -> {
                val paramCount = extractTupleCount(className)
                "Parameters$paramCount"
            }

            // Header types
            className == "dev.akif.tapik.http.Header" -> "Headers0"
            className.startsWith("dev.akif.tapik.http.Header<") -> {
                val headerCount = countGenericArguments(className)
                val innerType = extractInnerType(className)
                "Headers$headerCount<$innerType>"
            }
            className.startsWith("dev.akif.tapik.tuples.Tuple<Ldev.akif.tapik.http.Header<") -> {
                val headerCount = countGenericArguments(className)
                val innerType = extractInnerType(className)
                "Headers$headerCount<$innerType>"
            }

            // Body types
            className.startsWith("dev.akif.tapik.http.JsonBody<") -> {
                val bodyType = extractInnerType(className)
                "JsonBody<${cleanTypeName(bodyType)}>"
            }
            className.startsWith("dev.akif.tapik.http.Body<") -> {
                val bodyType = extractInnerType(className)
                "Body<$bodyType>"
            }
            className == "dev.akif.tapik.http.Body" -> "EmptyBody"

            // Output body types
            className.startsWith("dev.akif.tapik.tuples.Tuple<Ldev.akif.tapik.http.OutputBody<") -> {
                val outputBodyCount = countGenericArguments(className)
                val outputBodies = extractOutputBodies(className)
                "OutputBodies$outputBodyCount<$outputBodies>"
            }

            // Other tapik types - use simple names
            className.startsWith("dev.akif.tapik.") -> className.substringAfterLast('.')

            // Non-tapik types - keep fully qualified
            else -> className
        }
    }

    private fun countGenericArguments(typeString: String): Int {
        var count = 0
        var depth = 0
        var i = typeString.indexOf('<')
        if (i == -1) return 0

        i++ // Skip '<'
        while (i < typeString.length) {
            when (typeString[i]) {
                '<' -> depth++
                '>' -> {
                    if (depth == 0) break
                    depth--
                }
                ';' -> if (depth == 0) count++
            }
            i++
        }
        return count + 1 // +1 for the last argument
    }

    private fun extractInnerType(typeString: String): String {
        val start = typeString.indexOf('<') + 1
        val end = findMatchingBracket(typeString, start - 1)
        if (start in 1 until end) {
            val inner = typeString.substring(start, end)
            return parseInnerType(inner)
        }
        return "?"
    }

    private fun parseInnerType(inner: String): String {
        return when {
            inner.startsWith("L") -> inner.substring(1).replace('/', '.').substringAfterLast('.')
            else -> inner
        }
    }

    private fun extractOutputBodies(typeString: String): String {
        // Extract all OutputBody types from the tuple
        val bodies = mutableListOf<String>()
        var current = typeString.indexOf("Ldev/akif/tapik/http/OutputBody<")

        while (current >= 0) {
            val end = findMatchingBracket(typeString, current + "Ldev/akif/tapik/http/OutputBody<".length - 1)
            if (end > current) {
                val outputBody = typeString.substring(current, end + 1)
                val bodyType = extractInnerType(outputBody)
                bodies.add("JsonBody<$bodyType>")
            }
            current = typeString.indexOf("Ldev/akif/tapik/http/OutputBody<", current + 1)
        }

        return bodies.joinToString(", ")
    }

    private fun extractTupleCount(className: String): Int {
        // Extract the number from Tuple0, Tuple1, Tuple2, etc.
        val match = Regex("Tuple(\\d+)").find(className)
        return match?.groupValues?.get(1)?.toInt() ?: 0
    }


    private fun parseDescriptor(descriptor: String): String {
        return when {
            descriptor.startsWith("L") -> {
                val className = descriptor.substring(1, descriptor.length - 1).replace('/', '.')
                parseTapikType(className)
            }
            else -> descriptor
        }
    }

    fun simplifyTapikTypes(typeString: String): String {
        // Only simplify tapik types, preserve other types as-is
        return typeString
            .removePrefix("dev.akif.tapik.http")
            .removePrefix("dev.akif.tapik.tuples")
            .removePrefix("dev.akif.tapik.selections")
            .removePrefix("dev.akif.tapik")
    }

    private class ClassInfo {
        var packageName: String = ""
        val endpoints = mutableListOf<ResolvedEndpointInfo>()
    }
}
