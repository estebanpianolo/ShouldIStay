package com.etienne.shouldistay.main.weather.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaCellAPI {

    @GET("nowcast")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("unit_system") unitSystem: String,
        @Query("timestep") timeStep: Int,
        @Query("start_time") startTime: String,
        @Query("fields") fields: String?
    ): Single<List<WeatherEntity>>
}
