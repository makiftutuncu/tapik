package dev.akif.tapik.plugin.gradle

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.Provider

internal fun Configuration.tapikDependencyVersions(
    classpathName: String
): Provider<Map<String, String>> =
    incoming.resolutionResult.rootComponent.map { root ->
        root.allDependencies()
            .map(ResolvedDependencyResult::getSelected)
            .map(ResolvedComponentResult::getId)
            .filterIsInstance<ModuleComponentIdentifier>()
            .filter { id -> id.group == "dev.akif" && id.module.startsWith("tapik-") }
            .distinctBy { id -> id.group to id.module }
            .sortedWith(compareBy(ModuleComponentIdentifier::getGroup, ModuleComponentIdentifier::getModule))
            .associateTo(linkedMapOf()) { id ->
                "$classpathName|${id.group}:${id.module}" to id.version
            }
    }

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
