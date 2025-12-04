import buildlogic.BumpVersionTask

tasks.register<BumpVersionTask>("bumpVersion") {
    group = "release"
    description =
        "Updates the project version and doc snippets. Provide -PnewVersion; defaults to patch bump when omitted."
    currentVersion.set(provider { version.toString() })
    newVersion.set(providers.gradleProperty("newVersion"))
    rootPath.set(layout.projectDirectory.asFile.absolutePath)
    targetFiles.set(
        layout.projectDirectory
            .asFileTree
            .matching {
                include(
                    "README.md",
                    "AGENTS.md",
                    "mkdocs.yml",
                    "docs/**/*.md",
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
