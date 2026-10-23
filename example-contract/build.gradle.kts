import dev.akif.tapik.plugin.gradle.Mode
import dev.akif.tapik.plugin.gradle.OpenApiFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("dev.akif.tapik.plugin.gradle")
}

val tapikVersion = providers.gradleProperty("tapikVersion").getOrElse("0.6.0")

dependencies {
    implementation(platform("dev.akif:tapik-bom:$tapikVersion"))
    implementation("dev.akif:tapik-core")
    implementation("dev.akif:tapik-format-jackson")
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters.set(true)
    }
    sourceSets.test {
        kotlin.setSrcDirs(emptyList<String>())
    }
}

// tag::same-project[]
tapik {
    mode(Mode.SAME_PROJECT)

    openApi {
        version(tapikVersion)
        format(OpenApiFormat.YAML)
    }
}
// end::same-project[]
