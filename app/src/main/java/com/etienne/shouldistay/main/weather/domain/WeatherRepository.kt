package com.etienne.shouldistay.main.weather.domain

import com.etienne.shouldistay.domain.Location
import io.reactivex.rxjava3.core.Single

interface WeatherRepository {
    fun getWeather(location: Location): Single<List<Weather>>

}
