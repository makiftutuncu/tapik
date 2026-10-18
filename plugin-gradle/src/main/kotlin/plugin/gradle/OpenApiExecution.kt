package dev.akif.tapik.plugin.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

/** Serialization format for generated OpenAPI documents. */
enum class OpenApiFormat {
    /** YAML output. */
    YAML,

    /** JSON output. */
    JSON
}

/** Naming policy for generated OpenAPI components. */
enum class OpenApiComponentNaming {
    /** Use simple type names. */
    SIMPLE,

    /** Use qualified type names. */
    QUALIFIED
}

/** Typed configuration for one OpenAPI generation execution. */
abstract class OpenApiExecution @Inject constructor(name: String) : TapikExecution(name, "openapi") {
    /** Version written into the generated OpenAPI document. */
    @get:Input
    abstract val version: Property<String>

    /** OpenAPI serialization format. */
    @get:Input
    abstract val format: Property<OpenApiFormat>

    /** Whether generated OpenAPI is pretty-printed. */
    @get:Input
    abstract val pretty: Property<Boolean>

    /** OpenAPI component naming policy. */
    @get:Input
    abstract val componentNaming: Property<OpenApiComponentNaming>

    /** Optional output filename relative to tapik's documentation output directory. */
    @get:Input
    @get:Optional
    abstract val output: Property<String>

    init {
        format.convention(OpenApiFormat.YAML)
        pretty.convention(true)
        componentNaming.convention(OpenApiComponentNaming.SIMPLE)
    }

    /** Sets the OpenAPI document [version]. */
    fun version(version: String) = this.version.set(version)

    /** Sets the OpenAPI document [format]. */
    fun format(format: OpenApiFormat) = this.format.set(format)

    /** Sets whether generated OpenAPI is [pretty] printed. */
    fun pretty(pretty: Boolean) = this.pretty.set(pretty)

    /** Sets the OpenAPI [componentNaming] policy. */
    fun componentNaming(componentNaming: OpenApiComponentNaming) = this.componentNaming.set(componentNaming)

    /** Sets the relative OpenAPI output [filename]. */
    fun output(filename: String) = output.set(filename)

    internal override fun validateConfiguration() {
        super.validateConfiguration()
        require(version.isPresent && version.get().isNotBlank()) {
            "tapik OpenAPI execution '$name' requires a nonblank version"
        }
        require(!output.isPresent || output.get().isNotBlank()) {
            "tapik OpenAPI execution '$name' output must not be blank"
        }
    }
}
