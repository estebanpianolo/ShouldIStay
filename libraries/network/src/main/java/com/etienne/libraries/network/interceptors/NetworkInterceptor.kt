package com.etienne.libraries.network.interceptors

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NetworkInterceptor(
    private val queryParameters: List<Pair<String, String>>
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = chain.request().run {
        chain.proceed(newBuilder().url(url().addQueryParametersTuUrl()).build())
    }

    private fun HttpUrl.addQueryParametersTuUrl() = newBuilder()
        .apply { queryParameters.forEach { addQueryParameter(it.first, it.second) } }
        .build()

}

