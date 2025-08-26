package dev.akif.tapik.http

sealed interface Response<B>

data class EmptyResponse<B>(
    val status: Status
) : Response<B>

data class Response0<B>(
    val status: Status,
    val body: B
) : Response<B>

data class Response1<B, H1>(
    val status: Status,
    val body: B,
    val header1: H1
) : Response<B>

data class Response2<B, H1, H2>(
    val status: Status,
    val body: B,
    val header1: H1,
    val header2: H2
) : Response<B>
