package dev.akif.app

import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.http.*
import java.net.URI

data class UserResponse(val id: Long, val name: String, val status: String) {
    companion object {
        val codec: StringCodec<UserResponse>
            get() = TODO()
    }
}

data class CreateUserRequest(val name: String, val status: String) {
    companion object {
        val codec: StringCodec<CreateUserRequest>
            get() = TODO()
    }
}

data class APIError(val code: Int, val message: String) {
    companion object {
        val codec: StringCodec<APIError>
            get() = TODO()
    }
}

data class UserPage(val items: List<UserResponse>, val total: Long) {
    companion object {
        val codec: StringCodec<UserPage>
            get() = TODO()
    }
}

val usersURI: URIBuilder<PathVariables0, QueryParameters0> = root / "api" / "v1" / "users"

val userURI: URIBuilder<PathVariables1<Long>, QueryParameters0> = usersURI / path.long("id")

val listUsers: HttpEndpoint<PathVariables0, QueryParameters2<Int, Int>, Headers0, EmptyBody, Outputs1<JsonBody<UserPage>, Headers0>> =
    http
        .get
        .uri(usersURI + query.int("page") + query.int("perPage"))
        .output {
            jsonBody<UserPage>(UserPage.codec)
        }

val createUser: HttpEndpoint<PathVariables0, QueryParameters0, Headers1<MediaType>, JsonBody<CreateUserRequest>, Outputs2<JsonBody<UserResponse>, Headers1<URI>, JsonBody<APIError>, Headers0>> =
    http
        .post
        .uri(usersURI)
        .headers(header.Accept(MediaType.Json))
        .input(jsonBody<CreateUserRequest>(CreateUserRequest.codec))
        .output(Status.CREATED, header.Location) { jsonBody<UserResponse>(UserResponse.codec) }
        .output(Status.BAD_REQUEST) { jsonBody<APIError>(APIError.codec) }

val getUser: HttpEndpoint<PathVariables1<Long>, QueryParameters0, Headers1<Boolean>, EmptyBody, Outputs2<JsonBody<UserResponse>, Headers0, JsonBody<APIError>, Headers0>> =
    http
        .get
        .uri(userURI)
        .headers(header.boolean("Proxied"))
        .output { jsonBody<UserResponse>(UserResponse.codec) }
        .output(Status.NOT_FOUND) { jsonBody<APIError>(APIError.codec) }

val getUserStatus: HttpEndpoint<PathVariables1<Long>, QueryParameters0, Headers0, EmptyBody, Outputs2<StringBody, Headers0, JsonBody<APIError>, Headers0>> =
    http
        .get
        .uri(userURI / "status")
        .output { StringBody }
        .output(Status.NOT_FOUND) { jsonBody<APIError>(APIError.codec) }

val getUserAvatar: HttpEndpoint<PathVariables1<Long>, QueryParameters1<Int>, Headers0, EmptyBody, Outputs1<RawBody, Headers0>> =
    http
        .get
        .uri(userURI / "avatar" + query.int("size"))
        .output {
            rawBody(MediaType.Custom("image", "png"))
        }
