package dev.akif.tapik.plugin.maven

internal object TapikVersionCompatibility {
    fun requireCompatible(
        pluginVersion: String,
        projectVersions: Set<String>
    ) {
        require(pluginVersion.isNotBlank()) { "tapik Maven plugin version must not be blank" }
        require(projectVersions.none(String::isBlank)) { "tapik project dependency versions must not be blank" }
        require(projectVersions.all { version -> version == pluginVersion }) {
            "tapik version mismatch: Maven plugin uses $pluginVersion, but project dependencies use " +
                "${projectVersions.sorted().joinToString()}. " +
                "Align all dev.akif:tapik-* dependencies and dev.akif:tapik-plugin-maven to one version."
        }
    }
}
