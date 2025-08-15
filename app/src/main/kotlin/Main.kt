package dev.akif.app

import dev.akif.tapik.http.*
import java.net.URI

data class UserResponse(val id: Long, val name: String, val status: String)

data class CreateUserRequest(val name: String, val status: String)

data class APIError(val code: Int, val message: String)

val usersURI: URIBuilder<PathVariables0, QueryParameters0> = root / "api" / "v1" / "users"

val userURI: URIBuilder<PathVariables1<Long>, QueryParameters0> = usersURI / path<Long>("id")

val listUsers: HttpEndpoint<PathVariables0, QueryParameters2<Int, Int>, Headers0, EmptyBody, Outputs1<JsonBody<List<UserResponse>>, Headers0>> =
    http
        .get
        .uri(usersURI + query<Int>("page") + query<Int>("perPage"))
        .output {
            jsonBody<List<UserResponse>>()
        }

val createUser: HttpEndpoint<PathVariables0, QueryParameters0, Headers1<MediaType>, JsonBody<CreateUserRequest>, Outputs2<JsonBody<UserResponse>, Headers1<URI>, JsonBody<APIError>, Headers0>> =
    http
        .post
        .uri(usersURI)
        .headers(Header.Accept(MediaType.Json))
        .input(jsonBody<CreateUserRequest>())
        .output(Status.CREATED, Header.Location()) { jsonBody<UserResponse>() }
        .output(Status.BAD_REQUEST) { jsonBody<APIError>() }

val getUser: HttpEndpoint<PathVariables1<Long>, QueryParameters0, Headers1<Boolean>, EmptyBody, Outputs2<JsonBody<UserResponse>, Headers0, JsonBody<APIError>, Headers0>> =
    http
        .get
        .uri(userURI)
        .headers(header<Boolean>("Proxied"))
        .output { jsonBody<UserResponse>() }
        .output(Status.NOT_FOUND) { jsonBody<APIError>() }

val getUserStatus: HttpEndpoint<PathVariables1<Long>, QueryParameters0, Headers0, EmptyBody, Outputs2<StringBody, Headers0, JsonBody<APIError>, Headers0>> =
    http
        .get
        .uri(userURI / "status")
        .output { StringBody }
        .output(Status.NOT_FOUND) { jsonBody<APIError>() }

val getUserAvatar: HttpEndpoint<PathVariables1<Long>, QueryParameters1<Int>, Headers0, EmptyBody, Outputs1<RawBody, Headers0>> =
    http
        .get
        .uri(userURI / "avatar" + query<Int>("size"))
        .output {
            rawBody(MediaType.Custom("image", "png"))
        }
