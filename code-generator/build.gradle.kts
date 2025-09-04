plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

application {
    mainClass = "dev.akif.tapik.CodeGeneratorKt"
}

tasks.named<JavaExec>("run") {
    args = listOf("--limit", "16")
    workingDir(rootProject.projectDir)
}
