plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

application {
    mainClass = "dev.akif.tapik.CodeGeneratorKt"
}

fun generateCode(module: String) = tasks.register<JavaExec>("generate-$module") {
    description = "Run code generator for $module module"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(application.mainClass)
    workingDir(rootProject.projectDir)
    args("--limit", "10", "--module", module)
    dependsOn(tasks.named("classes"))
}

val core by generateCode("core")
val types by generateCode("types")
val springRestClient by generateCode("spring-restclient")
