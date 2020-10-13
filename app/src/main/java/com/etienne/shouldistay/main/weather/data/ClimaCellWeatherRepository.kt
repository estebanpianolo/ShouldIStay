package com.etienne.shouldistay.main.weather.data

import com.etienne.shouldistay.domain.Location
import com.etienne.shouldistay.main.weather.domain.Precipitation
import com.etienne.shouldistay.main.weather.domain.Temperature
import com.etienne.shouldistay.main.weather.domain.Weather
import com.etienne.shouldistay.main.weather.domain.WeatherRepository
import io.reactivex.rxjava3.core.Single

class ClimaCellWeatherRepository(private val api: ClimaCellAPI) : WeatherRepository {

    override fun getWeather(location: Location): Single<List<Weather>> = api.getWeather(
        location.latitude,
        location.longitude,
        "si",
        timeStep,
        "now",
        "precipitation,temp"
    ).map {
        it.toModel()
    }

    companion object {
        private const val timeStep = 5
    }
}


private fun List<WeatherEntity>.toModel() = run {
    map {
        Weather(
            Temperature(it.temp.value, it.temp.units),
            Precipitation(it.precipitation.value, it.precipitation.units),
            it.observationTime.value
        )
    }
}
