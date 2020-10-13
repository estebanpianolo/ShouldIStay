package com.etienne.shouldistay.main

import android.view.ViewGroup
import com.etienne.shouldistay.domain.LocationRetriever
import com.etienne.shouldistay.main.locationerror.LocationErrorViewComponent
import com.etienne.shouldistay.main.presentation.LocationPermissionResolver
import com.etienne.shouldistay.main.presentation.MainActivity
import com.etienne.shouldistay.main.presentation.MainCoordinator
import com.etienne.shouldistay.main.weather.WeatherViewComponent
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@MainActivityScope
@Subcomponent(modules = [MainComponent.Module::class])
interface MainComponent {
    fun inject(activity: MainActivity)

    val locationRetriever: LocationRetriever

    @dagger.Module(
        subcomponents = [
            LocationErrorViewComponent::class,
            WeatherViewComponent::class
        ]
    )
    class Module(
        private val parent: ViewGroup,
        private val locationPermissionResolver: LocationPermissionResolver
    ) {

        @Provides
        @MainActivityScope
        internal fun provideCoordinator(
            component: MainComponent,
            locationErrorViewBuilder: LocationErrorViewComponent.Builder,
            weatherViewBuilder: WeatherViewComponent.Builder
        ): MainCoordinator =
            MainCoordinator(
                parent,
                component,
                locationErrorViewBuilder,
                weatherViewBuilder,
                locationPermissionResolver
            )
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MainActivityScope
