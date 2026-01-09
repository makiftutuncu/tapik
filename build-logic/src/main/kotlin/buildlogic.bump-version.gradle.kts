import buildlogic.BumpVersionTask

tasks.register<BumpVersionTask>("bumpVersion") {
    group = "release"
    description =
        "Updates the project version and doc snippets. Provide -PnewVersion; defaults to patch bump when omitted."
    currentVersion.set(provider { version.toString() })
    newVersion.set(providers.gradleProperty("newVersion"))
    rootPath.set(layout.projectDirectory.asFile.absolutePath)
    autoCommit.set(
        providers.gradleProperty("bumpAutoCommit")
            .map { it.toBooleanStrictOrNull() ?: true }
            .orElse(true)
    )
    targetFiles.set(
        layout.projectDirectory
            .asFileTree
            .matching {
                include(
                    "README.md",
                    "AGENTS.md",
                    "antora-playbook.yml",
                    "antora-preview-playbook.yml",
                    "docs/antora.yml",
                    "docs/**/*.adoc",
                    "docs/**/*.html",
                    "**/*.gradle.kts",
                    "**/*.properties"
                )
                exclude(
                    "**/build/**",
                    "**/.gradle/**",
                    ".git/**",
                    "build-logic/build/**"
                )
            }
            .files
            .sorted()
            .map { layout.projectDirectory.file(it.toRelativeString(layout.projectDirectory.asFile)) }
    )
}
