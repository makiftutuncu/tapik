package dev.akif.app

import dev.akif.tapik.codec.*
import dev.akif.tapik.http.*
import dev.akif.tapik.tuple.*

data class UserResponse(val id: Long, val name: String, val status: String) {
    companion object {
        val codec: StringCodec<UserResponse> =
            StringCodec.unsafe("userResponse", { TODO() }) { TODO() }
    }
}

data class CreateUserRequest(val name: String, val status: String) {
    companion object {
        val codec: StringCodec<CreateUserRequest> =
            StringCodec.unsafe("createUserResponse", { TODO() }) { TODO() }
    }
}

data class APIError(val code: Int, val message: String) {
    companion object {
        val codec: StringCodec<APIError> =
            StringCodec.unsafe("apiError", { TODO() }) { TODO() }
    }
}

data class UserPage(val items: List<UserResponse>, val total: Long) {
    companion object {
        val codec: StringCodec<UserPage> =
            StringCodec.unsafe("userPage", { TODO() }) { TODO() }
    }
}

object Users {
    private val base = root / "api" / "v1" / "users"
    private val id = path.long("id")
    private val errorBody = jsonBody<APIError>(APIError.codec)

    val list =
        http(
            id = "listUsers",
            description = "List all users",
            details = "This endpoint lists all users with pagination."
        )
            .get
            .uri(base + query.int("page") + query.int("perPage"))
            .output {
                jsonBody<UserPage>(UserPage.codec)
            }

    val create =
        http(
            id = "createUser",
            description = "Create new user",
            details = "This endpoint creates a new user with given information."
        )
            .post
            .uri(base)
            .headers(header.Accept(MediaType.Json))
            .input(jsonBody<CreateUserRequest>(CreateUserRequest.codec))
            .output(Status.CREATED, header.Location) { jsonBody<UserResponse>(UserResponse.codec) }
            .output(Status.BAD_REQUEST) { errorBody }

    val get =
        http(
            id = "getUser",
            description = "Get a user",
            details = "This endpoint gets the user with given id."
        )
            .get
            .uri(base / id)
            .headers(header.boolean("Proxied"))
            .output { jsonBody<UserResponse>(UserResponse.codec) }
            .output(Status.NOT_FOUND) { errorBody }

    val getStatus =
        http(
            id = "getUserStatus",
            description = "Get status of a user",
            details = "This endpoint gets the status of the user with given id."
        )
            .get
            .uri(base / id / "status")
            .output { StringBody }
            .output(Status.NOT_FOUND) { errorBody }

    val getAvatar =
        http(
            id = "getUserAvatar",
            description = "Get avatar of a user",
            details = "This endpoint gets the avatar of the user with given id."
        )
            .get
            .uri(base / id / "avatar" + query.int("size"))
            .output {
                rawBody(MediaType.Custom("image", "png"))
            }

    val api: List<AnyHttpEndpoint> = listOf(list, create, get, getStatus, getAvatar)
}
