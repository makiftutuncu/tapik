import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
    id("dev.akif.tapik.gradle")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":jackson"))
    implementation(project(":spring-restclient"))
    implementation(libs.arrowCore)
    implementation(libs.springWeb)
}

application {
    mainClass = "dev.akif.app.RestClientExampleKt"
}

tapik {
    springRestClient {
        endpointPackages("dev.akif.app.example")
    }
}

ktlint {
    filter {
        exclude { it.file.path.contains("/generated/") }
    }
}
