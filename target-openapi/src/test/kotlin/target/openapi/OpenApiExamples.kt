package target.openapi.examples

// tag::direct-api[]
import dev.akif.tapik.Api
import dev.akif.tapik.target.openapi.OpenApi
import dev.akif.tapik.target.openapi.toYaml

fun renderOpenApi(api: Api): String {
    val document = OpenApi.from(api, version = "1.0")
    return document.toYaml()
}
// end::direct-api[]
