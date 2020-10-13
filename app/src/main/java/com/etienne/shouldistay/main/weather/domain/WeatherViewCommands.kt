package com.etienne.shouldistay.main.weather.domain

import com.etienne.libraries.archi.nucleus.Action
import com.etienne.libraries.archi.nucleus.Command
import com.etienne.shouldistay.domain.Location
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

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
    override fun execute(): Single<Action> = Single.just(
        WeatherViewInteractor.UpdateResults(
            weather[0].temperature,
            weather[0].precipitation
        ) as Action
    )
        .subscribeOn(computeScheduler)

}
