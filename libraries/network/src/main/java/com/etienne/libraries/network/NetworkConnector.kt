package com.etienne.libraries.network

import com.etienne.libraries.network.interceptors.ErrorInterceptor
import com.etienne.libraries.network.interceptors.NetworkInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal data class NetworkConnectorImpl(private val client: Retrofit) : NetworkConnector {
    override fun <T> create(service: Class<T>): T = client.create(service)
}

interface NetworkConnector {
    fun <T> create(service: Class<T>): T

    companion object {
        fun createNewConnector(
            baseUrl: String,
            queryParameter: List<Pair<String, String>>?,
            typeAdapters: List<Pair<Class<*>, TypeAdapter<*>>>?
        ): NetworkConnector {

            val converter = GsonConverterFactory.create(
                GsonBuilder().apply {
                    typeAdapters?.forEach { registerTypeAdapter(it.first, it.second) }
                }.create()
            )

            val client = OkHttpClient.Builder()
                .addInterceptor(ErrorInterceptor())
                .apply {
                    queryParameter?.let {
                        addNetworkInterceptor(NetworkInterceptor(queryParameter))
                    }
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build()

            return NetworkConnectorImpl(retrofit)
        }
    }
}
