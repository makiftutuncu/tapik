package dev.akif.tapik.plugin.maven

internal object TapikVersionCompatibility {
    fun requireCompatible(
        pluginVersion: String,
        projectVersions: Set<String>
    ) {
        require(pluginVersion.isNotBlank()) { "Tapik Maven plugin version must not be blank" }
        require(projectVersions.none(String::isBlank)) { "Tapik project dependency versions must not be blank" }
        require(projectVersions.all { version -> version == pluginVersion }) {
            "Tapik version mismatch: Maven plugin uses $pluginVersion, but project dependencies use " +
                "${projectVersions.sorted().joinToString()}. " +
                "Align all dev.akif:tapik-* dependencies and dev.akif:tapik-plugin-maven to one version."
        }
    }
}
