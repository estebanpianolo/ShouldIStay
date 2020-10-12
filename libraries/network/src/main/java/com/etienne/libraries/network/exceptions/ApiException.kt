package com.etienne.libraries.network.exceptions

import java.io.IOException

sealed class ApiException(open val body: String) : IOException() {
    data class Unauthorized(override val body: String) : ApiException(body)
    data class BadRequest(override val body: String) : ApiException(body)
    data class Forbidden(override val body: String) : ApiException(body)
    data class NotFound(override val body: String) : ApiException(body)
    data class Conflict(override val body: String) : ApiException(body)
    data class PreconditionFailed(override val body: String) : ApiException(body)
    data class TooManyRequests(override val body: String) : ApiException(body)
    data class Gone(override val body: String): ApiException(body)

    data class Server(override val body: String) : ApiException(body)
}


