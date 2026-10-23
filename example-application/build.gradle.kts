import dev.akif.tapik.plugin.gradle.Mode
import dev.akif.tapik.plugin.gradle.OpenApiFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
    application
    id("dev.akif.tapik.plugin.gradle")
}

val tapikVersion = providers.gradleProperty("tapikVersion").getOrElse("0.6.0")

dependencies {
    implementation(platform("dev.akif:tapik-bom:$tapikVersion"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.8"))
    testImplementation(platform("io.kotest:kotest-bom:6.2.4"))

    // tag::compiled-contract-dependencies[]
    implementation(project(":contract"))
    tapikContract(project(":contract"))
    // end::compiled-contract-dependencies[]

    implementation("dev.akif:tapik-target-spring-restclient")
    implementation("dev.akif:tapik-target-spring-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    testImplementation("io.kotest:kotest-runner-junit5-jvm")
    testImplementation("io.kotest:kotest-assertions-core-jvm")
    testImplementation("org.springframework:spring-test")
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        javaParameters.set(true)
    }
    sourceSets.test {
        kotlin.setSrcDirs(listOf(rootProject.file("application/src/test/kotlin")))
    }
}

application {
    mainClass.set("com.example.library.application.LibraryApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

// tag::compiled-contract[]
tapik {
    mode(Mode.COMPILED_CONTRACT)

    openApi("public") {
        version(tapikVersion)
        format(OpenApiFormat.YAML)
    }

    springRestClient("client") {
        packageName("com.example.library.generated")
    }

    springWebMvc("server") {
        packageName("com.example.library.generated")
    }
}
// end::compiled-contract[]
