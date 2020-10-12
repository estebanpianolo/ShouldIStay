package com.etienne.libraries.network.interceptors

import com.etienne.libraries.network.exceptions.ApiException
import com.etienne.libraries.network.exceptions.NetworkException
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response = try {
        chain.proceed(chain.request())
    } catch (exception: ApiException) { // can occur if RefreshToken fails for whatever reason.
        throw exception // so, should not throw a NetworkException in that case.
    } catch (exception: IOException) {
        throw NetworkException(exception)
    }.apply {
        if (!isSuccessful) {
            checkApiError(code(), body()?.string())
        }
    }

    @Throws(ApiException::class)
    private fun checkApiError(code: Int, body: String?) {
        body?.let {
            when (code) {
                400 -> throw ApiException.BadRequest(body)
                401 -> throw ApiException.Unauthorized(body)
                403 -> throw ApiException.Forbidden(body)
                404 -> throw ApiException.NotFound(body)
                409 -> throw ApiException.Conflict(body)
                410 -> throw ApiException.Gone(body)
                412 -> throw ApiException.PreconditionFailed(body)
                429 -> throw ApiException.TooManyRequests(body)
                in 500..599 -> throw ApiException.Server(body)
                else -> throw ApiException.Server(body)
            }
        } ?: throw ApiException.Server("")
    }
}
