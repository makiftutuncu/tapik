package dev.akif.tapik.common.format

import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import java.time.*

/** Returns tapik's scalar schema for the built-in JVM [type], or `null`. */
fun builtInSchema(type: Class<*>): ScalarSchema? = builtInSchema(type.name)

/** Returns tapik's scalar schema for the built-in JVM or Kotlin serialization [typeName], or `null`. */
fun builtInSchema(typeName: String): ScalarSchema? = BUILT_IN_SCHEMAS[typeName]

private val BUILT_IN_SCHEMAS: Map<String, ScalarSchema> =
    buildMap {
        register(ScalarSchema(SchemaType.BOOLEAN), "boolean", Boolean::class.javaObjectType.name, "kotlin.Boolean")
        register(ScalarSchema(SchemaType.INTEGER, "int32"), "byte", Byte::class.javaObjectType.name, "kotlin.Byte")
        register(ScalarSchema(SchemaType.INTEGER, "int32"), "short", Short::class.javaObjectType.name, "kotlin.Short")
        register(ScalarSchema(SchemaType.INTEGER, "int32"), "int", Int::class.javaObjectType.name, "kotlin.Int")
        register(ScalarSchema(SchemaType.INTEGER, "int64"), "long", Long::class.javaObjectType.name, "kotlin.Long")
        register(ScalarSchema(SchemaType.NUMBER, "float"), "float", Float::class.javaObjectType.name, "kotlin.Float")
        register(ScalarSchema(SchemaType.NUMBER, "double"), "double", Double::class.javaObjectType.name, "kotlin.Double")
        register(ScalarSchema(SchemaType.STRING), "char", Char::class.javaObjectType.name, "kotlin.Char")
        register(ScalarSchema(SchemaType.STRING), String::class.java.name, "kotlin.String")
        register(ScalarSchema(SchemaType.STRING, "date"), LocalDate::class.java.name)
        register(ScalarSchema(SchemaType.STRING, "time-local"), LocalTime::class.java.name)
        register(ScalarSchema(SchemaType.STRING, "date-time-local"), LocalDateTime::class.java.name)
        register(ScalarSchema(SchemaType.STRING, "time"), OffsetTime::class.java.name)
        register(
            ScalarSchema(SchemaType.STRING, "date-time"),
            OffsetDateTime::class.java.name,
            ZonedDateTime::class.java.name,
            Instant::class.java.name
        )
        register(ScalarSchema(SchemaType.STRING, "duration"), Duration::class.java.name, Period::class.java.name)
    }

private fun MutableMap<String, ScalarSchema>.register(
    schema: ScalarSchema,
    vararg typeNames: String
) {
    typeNames.forEach { typeName -> put(typeName, schema) }
}
