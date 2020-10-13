package com.etienne.shouldistay.main.weather.presentation

import com.etienne.libraries.archi.coordinator.Coordinator
import com.etienne.shouldistay.main.weather.WeatherViewComponent

class WeatherViewCoordinator(component: WeatherViewComponent) : Coordinator<WeatherViewComponent>(component){
    override fun start() {
        component.viewHolder().addOnRootView()
    }

    override fun onRelease() {
        component.viewHolder().removeFromRootView()
        component.interactor().clear()
    }
}
