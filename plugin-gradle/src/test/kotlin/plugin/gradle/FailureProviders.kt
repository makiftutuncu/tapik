package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

private class DuplicateApi : Api("Duplicate")

class DuplicateApiRegistryOne : ApiRegistry {
    override val apis: List<Api> = listOf(DuplicateApi())
}

class DuplicateApiRegistryTwo : ApiRegistry {
    override val apis: List<Api> = listOf(DuplicateApi())
}

private object DuplicateTarget : GenerationTarget {
    override val id: String = "duplicate"

    override fun generate(request: GenerationRequest): GenerationResult = GenerationResult(emptyList())
}

class DuplicateTargetRegistryOne : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(DuplicateTarget)
}

class DuplicateTargetRegistryTwo : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(DuplicateTarget)
}

class LinkageFailingTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget>
        get() = throw NoClassDefFoundError("missing target dependency")
}

class LinkageFailingApiRegistry : ApiRegistry {
    override val apis: List<Api>
        get() = throw NoClassDefFoundError("missing contract dependency")
}

private object ThrowingTarget : GenerationTarget {
    override val id: String = "throwing"

    override fun generate(request: GenerationRequest): GenerationResult =
        throw IllegalArgumentException("target failure")
}

class ThrowingTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(ThrowingTarget)
}
