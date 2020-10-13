package com.etienne.shouldistay.main.weather.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
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

class WeatherViewHolder(
    rootView: ViewGroup,
    interactor: NucleusInteractor<WeatherViewInteractor.State>
) :
    NucleusViewHolder<ConstraintLayout, WeatherViewInteractor.State>(
        rootView,
        R.layout.view_weather,
        interactor
    ) {

    override val dispatcher: Dispatcher = dispatch(
    )

    override val modelWatchConfigurator: ModelWatcherConfigurator<WeatherViewInteractor.State> = {
        watch {
            when (it) {
                is WeatherViewInteractor.State.Result -> handleResults(
                    it.temperature,
                    it.precipitation
                )
                else -> handleLoading()
            }
        }
    }

    private fun handleResults(temperature: Temperature, precipitation: Precipitation) {
        Handler(Looper.getMainLooper()).run {
            view.current_temperature_value.text = context.resources.getString(R.string.current_temperature_value, temperature.value, temperature.units)
            view.current_precipitation_value.text = context.resources.getString(R.string.current_precipitation_value, precipitation.value, precipitation.units)
            view.current_temperature_progress.visibility = View.INVISIBLE
            view.current_precipitation_progress.visibility = View.INVISIBLE
        }
    }

    private fun handleLoading() {
        Handler(Looper.getMainLooper()).run {
            view.current_temperature_value.text = ""
            view.current_precipitation_value.text = ""
            view.current_temperature_progress.visibility = View.VISIBLE
            view.current_precipitation_progress.visibility = View.VISIBLE
        }
    }
}

