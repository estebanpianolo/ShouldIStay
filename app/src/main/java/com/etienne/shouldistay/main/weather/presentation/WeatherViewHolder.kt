package com.etienne.shouldistay.main.weather.presentation

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.etienne.libraries.archi.nucleus.NucleusInteractor
import com.etienne.libraries.archi.view.Dispatcher
import com.etienne.libraries.archi.view.ModelWatcherConfigurator
import com.etienne.libraries.archi.view.NucleusViewHolder
import com.etienne.libraries.archi.view.dispatch
import com.etienne.shouldistay.R
import com.etienne.shouldistay.main.weather.domain.Precipitation
import com.etienne.shouldistay.main.weather.domain.Temperature
import com.etienne.shouldistay.main.weather.domain.WeatherViewInteractor
import kotlinx.android.synthetic.main.view_weather.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class WeatherViewHolder(
    rootView: ViewGroup,
    interactor: NucleusInteractor<WeatherViewInteractor.State>
) :
    NucleusViewHolder<ConstraintLayout, WeatherViewInteractor.State>(
        rootView,
        R.layout.view_weather,
        interactor
    ) {

    init {
        view.time_slot_title.text = context.resources.getString(
            R.string.time_slot_title,
            WeatherViewInteractor.timeSlotSpan
        )
    }

    override val dispatcher: Dispatcher = dispatch(
    )

    override val modelWatchConfigurator: ModelWatcherConfigurator<WeatherViewInteractor.State> = {
        watch {
            when (it) {
                is WeatherViewInteractor.State.Result -> handleResults(
                    it.temperature,
                    it.precipitation,
                    it.nextTimeSlot
                )
                else -> handleLoading()
            }
        }
    }

    private fun handleResults(
        temperature: Temperature,
        precipitation: Precipitation,
        nextTimeSlot: WeatherViewInteractor.TimeSlot
    ) {
        Handler(Looper.getMainLooper()).run {
            view.current_temperature_value.text = context.resources.getString(
                R.string.current_temperature_value,
                temperature.value,
                temperature.units
            )
            view.current_precipitation_value.text = context.resources.getString(
                R.string.current_precipitation_value,
                precipitation.value,
                precipitation.units
            )
            view.time_slot_value.text = nextTimeSlot.mapToText(context.resources)
            view.current_temperature_progress.visibility = View.INVISIBLE
            view.current_precipitation_progress.visibility = View.INVISIBLE
            view.time_slot_progress.visibility = View.INVISIBLE
        }
    }

    private fun handleLoading() {
        Handler(Looper.getMainLooper()).run {
            view.current_temperature_value.text = ""
            view.current_precipitation_value.text = ""
            view.time_slot_value.text = ""
            view.current_temperature_progress.visibility = View.VISIBLE
            view.current_precipitation_progress.visibility = View.VISIBLE
            view.time_slot_progress.visibility = View.VISIBLE
        }
    }
}

private fun WeatherViewInteractor.TimeSlot.mapToText(resources: Resources) = when(this) {
    WeatherViewInteractor.TimeSlot.Now -> resources.getString(R.string.time_slot_now)
    is WeatherViewInteractor.TimeSlot.In -> resources.getString(R.string.time_slot_in, minutes)
    is WeatherViewInteractor.TimeSlot.At -> {
        val formattedTime = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(date)
        resources.getString(R.string.time_slot_at, formattedTime)
    }
    WeatherViewInteractor.TimeSlot.Never -> resources.getString(R.string.time_slot_never)
}

