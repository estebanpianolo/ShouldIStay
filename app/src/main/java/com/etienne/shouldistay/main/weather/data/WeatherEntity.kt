package com.etienne.shouldistay.main.weather.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class WeatherEntity(
    @SerializedName("temp") val temp: TempEntity,
    @SerializedName("precipitation") val precipitation: PrecipitationEntity,
    @SerializedName("observation_time") val observationTime: ObservationTimeEntity
)

data class TempEntity(
    @SerializedName("value") val value: Double,
    @SerializedName("units") val units: String
)

data class PrecipitationEntity(
    @SerializedName("value") val value: Double,
    @SerializedName("units") val units: String
)

data class ObservationTimeEntity(
    @SerializedName("value") val value: Date
)
