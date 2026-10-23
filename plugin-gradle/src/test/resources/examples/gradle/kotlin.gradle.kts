import dev.akif.tapik.plugin.gradle.Mode
import dev.akif.tapik.plugin.gradle.OpenApiComponentNaming
import dev.akif.tapik.plugin.gradle.OpenApiFormat

// tag::plugin-application[]
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("dev.akif.tapik.plugin.gradle")
}
// end::plugin-application[]

// tag::dependency-configurations[]
dependencies {
    tapikContract("com.example:library-contract:1.0.0")
    tapikTarget("com.example:company-tapik-target:1.2.0")
}
// end::dependency-configurations[]

// tag::kotlin-configuration[]
tapik {
    mode(Mode.COMPILED_CONTRACT)
    outputRoot(layout.buildDirectory.dir("generated/contracts"))

    openApi("public") {
        includeApis("Catalog")
        version("1.0.0")
        format(OpenApiFormat.JSON)
        componentNaming(OpenApiComponentNaming.QUALIFIED)
    }

    springRestClient("catalogClient") {
        packageName("com.example.generated")
        clientSuffix("Client")
    }

    target("companyDocs", "company-docs") {
        value("audience", "public")
        list("formats", "html", "markdown")
        objectValue("branding") {
            value("compact", true)
            list("audiences") {
                value("public")
                objectValue {
                    value("name", "partners")
                }
            }
        }
    }
}
// end::kotlin-configuration[]
