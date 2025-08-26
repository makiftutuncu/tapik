package dev.akif.app

import dev.akif.tapik.http.*
import dev.akif.tapik.jackson.*

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

object Users {
    private val base = root / "api" / "v1" / "users"
    private val id = path.long("id")
    private val errorResponse = jsonBody<APIError>("error")
    private val userResponse = jsonBody<UserResponse>("response")

    val list =
        http(
            id = "listUsers",
            description = "List all users",
            details = "This endpoint lists all users with pagination."
        ).get
            .uri(base + query.int("page") + query.int("perPage"))
            .output {
                jsonBody<UserPage>("response")
            }

    val create =
        http(
            id = "createUser",
            description = "Create new user",
            details = "This endpoint creates a new user with given information."
        ).post
            .uri(base)
            .headers(header.Accept(MediaType.Json))
            .input(jsonBody<CreateUserRequest>("createUserRequest"))
            .output(Status.CREATED, header.Location) { userResponse }
            .output(Status.BAD_REQUEST) { errorResponse }

    val get =
        http(
            id = "getUser",
            description = "Get a user",
            details = "This endpoint gets the user with given id."
        ).get
            .uri(base / id)
            .headers(header.boolean("Proxied"))
            .output { userResponse }
            .output(Status.NOT_FOUND) { errorResponse }

    val getStatus =
        http(
            id = "getUserStatus",
            description = "Get status of a user",
            details = "This endpoint gets the status of the user with given id."
        ).get
            .uri(base / id / "status")
            .output { stringBody() }
            .output(Status.NOT_FOUND) { errorResponse }

    val getAvatar =
        http(
            id = "getUserAvatar",
            description = "Get avatar of a user",
            details = "This endpoint gets the avatar of the user with given id."
        ).get
            .uri(base / id / "avatar" + query.int("size"))
            .output {
                rawBody(MediaType.Custom("image", "png"))
            }

    val api: List<AnyHttpEndpoint> = listOf(list, create, get, getStatus, getAvatar)
}
