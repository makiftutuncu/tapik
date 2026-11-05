package dev.akif.tapik.plugin.gradle

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.Provider

internal fun Configuration.tapikDependencyVersions(
    classpathName: String
): Provider<Map<String, String>> =
    incoming.resolutionResult.rootComponent.map { root ->
        root.allDependencies()
            .map(ResolvedDependencyResult::getSelected)
            .distinctBy(ResolvedComponentResult::getId)
            .mapNotNull(ResolvedComponentResult::tapikComponent)
            .sortedWith(
                compareBy(
                    ResolvedTapikComponent::group,
                    ResolvedTapikComponent::module,
                    ResolvedTapikComponent::version,
                    ResolvedTapikComponent::identity
                )
            )
            .associateTo(linkedMapOf()) { component ->
                buildString {
                    append(classpathName)
                    append(" contains ")
                    append(component.group)
                    append(':')
                    append(component.module)
                    append(':')
                    append(component.version)
                    append(" from ")
                    append(component.identity)
                } to component.version
            }
    }

private fun ResolvedComponentResult.tapikComponent(): ResolvedTapikComponent? {
    val component =
        when (val componentId = id) {
            is ModuleComponentIdentifier ->
                ResolvedTapikComponent(
                    group = componentId.group,
                    module = componentId.module,
                    version = componentId.version,
                    identity = "external module ${componentId.displayName}"
                )
            is ProjectComponentIdentifier ->
                moduleVersion?.let { coordinates ->
                    ResolvedTapikComponent(
                        group = coordinates.group,
                        module = coordinates.name,
                        version = coordinates.version,
                        identity = componentId.identity()
                    )
                } ?: return null
            else -> return null
        }
    return component.takeIf { candidate ->
        candidate.group == "dev.akif" && candidate.module.startsWith("tapik-")
    }
}

private fun ProjectComponentIdentifier.identity(): String =
    if (build.buildPath == ":") {
        "project $projectPath"
    } else {
        "included build ${build.buildPath} project $projectPath"
    }

private data class ResolvedTapikComponent(
    val group: String,
    val module: String,
    val version: String,
    val identity: String
)

private fun ResolvedComponentResult.allDependencies(): List<ResolvedDependencyResult> {
    val visited = mutableSetOf<ComponentIdentifier>()
    val dependencies = mutableListOf<ResolvedDependencyResult>()

    fun visit(component: ResolvedComponentResult) {
        if (!visited.add(component.id)) return
        component.dependencies.filterIsInstance<ResolvedDependencyResult>().forEach { dependency ->
            dependencies += dependency
            visit(dependency.selected)
        }
    }

    visit(this)
    return dependencies
}
