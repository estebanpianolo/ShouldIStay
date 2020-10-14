package com.etienne.shouldistay.main.weather.domain

import com.etienne.libraries.archi.nucleus.*
import com.etienne.libraries.pratik.rx.relay
import com.etienne.shouldistay.domain.Location
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.*

class WeatherViewInteractorImpl(
    location: Observable<Location>,
    weatherRepository: WeatherRepository,
    ioScheduler: Scheduler,
    computeScheduler: Scheduler
) :
    NucleusInteractorImpl<WeatherViewInteractor.State>(WeatherViewInteractor.State.WaitingForLocation),
    WeatherViewInteractor {

    private val disposables = CompositeDisposable()

    init {
        location.map { WeatherViewInteractor.UpdateWeather(it) }.relay(actions).addTo(disposables)
    }

    override val reducerConfigurator: NucleusReducerConfigurator<WeatherViewInteractor.State> = {
        WeatherViewInteractor.UpdateWeather::class changesBoth {
            WeatherViewInteractor.State.UpdatingWeather to
                    UpdateWeatherCommand(weatherRepository, it.location, ioScheduler)
        }
        WeatherViewInteractor.ComputeResults::class changesBoth {
            WeatherViewInteractor.State.ComputingResults to
                    ComputeResultsCommand(it.weather, computeScheduler)
        }
        WeatherViewInteractor.UpdateResults::class changesState {
            WeatherViewInteractor.State.Result(it.temperature, it.precipitation, it.nextTimeSlot)
        }
    }
        override fun clear() {
        super.clear()
        disposables.clear()
    }
}

interface WeatherViewInteractor : NucleusInteractor<WeatherViewInteractor.State>,
    ClearableInteractor {

    sealed class State {
        object WaitingForLocation : State()
        object UpdatingWeather : State()
        object ComputingResults : State()
        data class Result(
            val temperature: Temperature,
            val precipitation: Precipitation,
            val nextTimeSlot : TimeSlot
        ) : State()
    }

    data class UpdateWeather(val location: Location) : Action
    data class UpdateWeatherError(val throwable: Throwable) : Action
    data class ComputeResults(val location: Location, val weather: List<Weather>) : Action
    data class UpdateResults(
        val temperature: Temperature,
        val precipitation: Precipitation,
        val nextTimeSlot : TimeSlot
    ) : Action

    sealed class TimeSlot {
        object Now : TimeSlot()
        data class In(val minutes: Int) : TimeSlot()
        data class At(val date: Date) : TimeSlot()
        object Never : TimeSlot()
    }


    companion object {
        const val timeSlotSpan = 15 //in minutes
    }
}
