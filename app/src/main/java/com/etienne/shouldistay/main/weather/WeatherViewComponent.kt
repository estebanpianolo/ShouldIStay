package com.etienne.shouldistay.main.weather

import android.view.ViewGroup
import com.etienne.libraries.network.NetworkConnector
import com.etienne.shouldistay.ComputationScheduler
import com.etienne.shouldistay.IOScheduler
import com.etienne.shouldistay.domain.Location
import com.etienne.shouldistay.main.locationerror.LocationErrorViewScope
import com.etienne.shouldistay.main.weather.data.ClimaCellAPI
import com.etienne.shouldistay.main.weather.data.ClimaCellWeatherRepository
import com.etienne.shouldistay.main.weather.domain.WeatherRepository
import com.etienne.shouldistay.main.weather.domain.WeatherViewInteractor
import com.etienne.shouldistay.main.weather.domain.WeatherViewInteractorImpl
import com.etienne.shouldistay.main.weather.presentation.WeatherViewCoordinator
import com.etienne.shouldistay.main.weather.presentation.WeatherViewHolder
import dagger.Provides
import dagger.Subcomponent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Scope

@WeatherViewScope
@Subcomponent(modules = [WeatherViewComponent.Module::class])
interface WeatherViewComponent {

    fun coordinator(): WeatherViewCoordinator
    fun viewHolder(): WeatherViewHolder
    fun interactor(): WeatherViewInteractor

    @Subcomponent.Builder
    interface Builder {
        fun module(module: Module): Builder
        fun build(): WeatherViewComponent
    }

    @dagger.Module
    class Module(private val container: ViewGroup, private val location: Observable<Location>) {

        @Provides
        @WeatherViewScope
        internal fun provideCoordinator(
            component: WeatherViewComponent,
        ): WeatherViewCoordinator = WeatherViewCoordinator(component)

        @Provides
        @WeatherViewScope
        internal fun provideWeatherInteractor(
            weatherRepository: WeatherRepository,
            @IOScheduler ioScheduler: Scheduler,
            @ComputationScheduler computeScheduler: Scheduler
        ): WeatherViewInteractorImpl =
            WeatherViewInteractorImpl(location, weatherRepository, ioScheduler, computeScheduler)

        @Provides
        @WeatherViewScope
        internal fun provideInteractor(interactor: WeatherViewInteractorImpl): WeatherViewInteractor =
            interactor

        @Provides
        @WeatherViewScope
        internal fun provideViewHolder(interactor: WeatherViewInteractor): WeatherViewHolder =
            WeatherViewHolder(container, interactor)

        @Provides
        @WeatherViewScope
        internal fun provideWeatherApi(networkConnector: NetworkConnector): ClimaCellAPI =
            networkConnector.create(ClimaCellAPI::class.java)

        @Provides
        @WeatherViewScope
        internal fun provideWeatherRepository(api: ClimaCellAPI): WeatherRepository =
            ClimaCellWeatherRepository(api)

    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class WeatherViewScope
