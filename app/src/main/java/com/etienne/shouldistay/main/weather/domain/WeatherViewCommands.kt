package com.etienne.shouldistay.main.weather.domain

import com.etienne.libraries.archi.nucleus.Action
import com.etienne.libraries.archi.nucleus.Command
import com.etienne.shouldistay.domain.Location
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.util.*

@Suppress("USELESS_CAST")
class UpdateWeatherCommand(
    private val weatherRepository: WeatherRepository,
    private val location: Location,
    private val ioScheduler: @NonNull Scheduler
) : Command {
    override fun execute(): Single<Action> =
        weatherRepository.getWeather(location)
            .map { WeatherViewInteractor.ComputeResults(location, it) as Action }
            .onErrorReturn { WeatherViewInteractor.UpdateWeatherError(it) }
            .subscribeOn(ioScheduler)
}


class ComputeResultsCommand(
    private val weather: List<Weather>,
    private val computeScheduler: Scheduler
) : Command {
    override fun execute(): Single<Action> = Single.create<Action> {

        findNextTimeSlot(weather)
        it.onSuccess(
            WeatherViewInteractor.UpdateResults(
                weather[0].temperature,
                weather[0].precipitation,
                findNextTimeSlot(weather)
            ) as Action
        )
    }
        .subscribeOn(computeScheduler)

    private fun findNextTimeSlot(weather: List<Weather>): WeatherViewInteractor.TimeSlot {
        var start = -1L
        var current: Long
        for (observation in weather) {
            if (start == -1L && observation.precipitation.value <= noPrecipitationThreshold) {
                start = observation.observationTime.time
            } else if (observation.precipitation.value > noPrecipitationThreshold) {
                start = -1L
            }
            current = observation.observationTime.time
            if (start != -1L && current - start >= WeatherViewInteractor.timeSlotSpan * 60 * 1000) {
                break
            }
        }
        val waitingTime = ((start - weather[0].observationTime.time) / (60 * 1000)).toInt()
        return if (waitingTime < 0) {
            WeatherViewInteractor.TimeSlot.Never
        } else {
            when {
                waitingTime == 0 -> WeatherViewInteractor.TimeSlot.Now
                waitingTime < 60 -> WeatherViewInteractor.TimeSlot.In(waitingTime)
                else -> WeatherViewInteractor.TimeSlot.At(Date(start))
            }
        }
    }

    companion object {
        const val noPrecipitationThreshold = 0.02 // in mm/h
    }
}
