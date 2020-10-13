package com.etienne.shouldistay.main.weather.domain

import java.util.*

data class Weather(
    val temperature: Temperature,
    val precipitation: Precipitation,
    val observationTime: Date
)


data class Temperature(
    val value: Double,
    val units: String
)

data class Precipitation(
    val value: Double,
    val units: String
)
