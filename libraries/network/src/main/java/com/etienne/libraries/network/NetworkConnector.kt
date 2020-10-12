package com.etienne.libraries.network

import com.etienne.libraries.network.interceptors.ErrorInterceptor
import com.google.gson.GsonBuilder
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
        fun createNewConnector(baseUrl: String): NetworkConnector {

            val converter = GsonConverterFactory.create(GsonBuilder().create())

            val client = OkHttpClient.Builder()
                .addInterceptor(ErrorInterceptor())
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
