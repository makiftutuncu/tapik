import dev.akif.tapik.plugin.gradle.Mode
import dev.akif.tapik.plugin.gradle.OpenApiComponentNaming
import dev.akif.tapik.plugin.gradle.OpenApiFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("dev.akif.tapik.plugin.gradle")
}

val tapikVersion = providers.gradleProperty("tapikVersion").get()

dependencies {
    tapikContract("dev.akif:tapik-example-contract:$tapikVersion")
}

tapik {
    mode(Mode.COMPILED_CONTRACT)
    outputRoot(layout.buildDirectory.dir("gradle/raw"))

    openApi("openapi-default") {
        version(tapikVersion)
    }
    openApi("openapi-explicit") {
        version("$tapikVersion-explicit")
        format(OpenApiFormat.JSON)
        pretty(false)
        componentNaming(OpenApiComponentNaming.QUALIFIED)
        output("explicit/{api}.json")
    }
    springRestClient("restclient-default") {}
    springRestClient("restclient-explicit") {
        packageName("com.example.parity")
        clientSuffix("HttpClient")
    }
    springWebMvc("webmvc-default") {}
    springWebMvc("webmvc-explicit") {
        packageName("com.example.parity")
        serverSuffix("HttpServer")
        controllerSuffix("Controller")
    }
}
