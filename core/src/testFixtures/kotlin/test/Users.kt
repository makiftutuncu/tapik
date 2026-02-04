package dev.akif.tapik.test

import dev.akif.tapik.*
import dev.akif.tapik.codec.ByteArrayCodec

data class UserResponse(
    val id: Long,
    val name: String,
    val status: String
)

data class CreateUserRequest(
    val name: String,
    val status: String
)

data class APIError(
    val code: Int,
    val message: String
)

data class UserPage(
    val items: List<UserResponse>,
    val total: Long
)

inline fun <reified T : Any> testJsonBody(name: String): Body<T> =
    JsonBody(
        ByteArrayCodec.unsafe(
            name,
            { ByteArray(0) },
            { error("") }
        ),
        name
    )

object Users : API {
    private val base = "api" / "v1" / "users"
    private val id = path.long("id")
    private val errorResponse = testJsonBody<APIError>("error")
    private val userResponse = testJsonBody<UserResponse>("response")

    val list by endpoint(description = "List all users", details = "This endpoint lists all users with pagination.") {
        get(base + query.int("page").optional(0) + query.int("perPage").optional(10))
            .output { testJsonBody<UserPage>("response") }
    }

    val create by endpoint(
        description = "Create new user",
        details = "This endpoint creates a new user with given information."
    ) {
        post(base).apply {
            input(header.Accept(MediaType.Json, MediaType.PlainText)) {
                testJsonBody<CreateUserRequest>("createUserRequest")
            }
            output(
                status = Status.CREATED,
                headers = headersOf(Header.Location)
            ) { userResponse }
            output(Status.BAD_REQUEST) { errorResponse }
        }
    }

    val get by endpoint(description = "Get a user", details = "This endpoint gets the user with given id.") {
        get(base / id).apply {
            input(header.boolean("Proxied"))
            output { userResponse }
            output(Status.NOT_FOUND) { errorResponse }
        }
    }

    val getStatus by endpoint(
        description = "Get status of a user",
        details = "This endpoint gets the status of the user with given id."
    ) {
        get(base / id / "status").apply {
            output { stringBody() }
            output(Status.NOT_FOUND) { errorResponse }
        }
    }

    val getAvatar by endpoint(
        description = "Get avatar of a user",
        details = "This endpoint gets the avatar of the user with given id."
    ) {
        get(base / id / "avatar" + query.int("size")).apply {
            output { rawBody(mediaType = MediaType.Custom("image", "png")) }
        }
    }

    val test =
        endpoint("testEndpoint3") {
            post(path.boolean("on")).apply {
                input(Header.Accept)
                output(
                    Status.BAD_REQUEST,
                    headersOf(Header.ContentType(MediaType.Json))
                ) { testJsonBody<Map<String, String>>("error") }
                output(anyStatus(Status.UNAUTHORIZED, Status.FORBIDDEN)) { stringBody("authError") }
                output(unmatchedStatus) { EmptyBody }
            }
        }
}
