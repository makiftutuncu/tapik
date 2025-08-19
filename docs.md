## listUsers: List all users

This endpoint lists all users with pagination.

`GET /api/v1/users?{page: Int}&{perPage: Int}`

### URI Parameters

| Name | Type | Position |
| ---- | ---- | -------- |
| page | Int | In Query |
| perPage | Int | In Query |

## createUser: Create new user

This endpoint creates a new user with given information.

`POST /api/v1/users`



### Headers

| Name | Type |
| ---- | ---- |
| Accept | MediaType |

### Input

`CreateUserRequest` as `Json`

## getUser: Get a user

This endpoint gets the user with given id.

`GET /api/v1/users/{id: Long}`

### URI Parameters

| Name | Type | Position |
| ---- | ---- | -------- |
| id | Long | In Path |

### Headers

| Name | Type |
| ---- | ---- |
| Proxied | Boolean |

## getUserStatus: Get status of a user

This endpoint gets the status of the user with given id.

`GET /api/v1/users/{id: Long}/status`

### URI Parameters

| Name | Type | Position |
| ---- | ---- | -------- |
| id | Long | In Path |

## getUserAvatar: Get avatar of a user

This endpoint gets the avatar of the user with given id.

`GET /api/v1/users/{id: Long}/avatar?{size: Int}`

### URI Parameters

| Name | Type | Position |
| ---- | ---- | -------- |
| id | Long | In Path |
| size | Int | In Query |
