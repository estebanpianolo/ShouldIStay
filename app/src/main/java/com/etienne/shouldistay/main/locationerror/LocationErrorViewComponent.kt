package com.etienne.shouldistay.main.locationerror

import android.view.ViewGroup
import com.etienne.shouldistay.domain.LocationUpdater
import com.etienne.shouldistay.main.locationerror.domain.LocationErrorViewInteractor
import com.etienne.shouldistay.main.locationerror.domain.LocationErrorViewInteractorImpl
import com.etienne.shouldistay.main.locationerror.presentation.LocationErrorViewCoordinator
import com.etienne.shouldistay.main.locationerror.presentation.LocationErrorViewHolder
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@LocationErrorViewScope
@Subcomponent(modules = [LocationErrorViewComponent.Module::class])
interface LocationErrorViewComponent {

    fun coordinator(): LocationErrorViewCoordinator
    fun viewHolder(): LocationErrorViewHolder
    fun interactor(): LocationErrorViewInteractor

    @Subcomponent.Builder
    interface Builder {
        fun module(module: Module): Builder
        fun build(): LocationErrorViewComponent
    }

    @dagger.Module
    class Module(private val container: ViewGroup) {

        @Provides
        @LocationErrorViewScope
        internal fun provideCoordinator(
            component: LocationErrorViewComponent,
        ): LocationErrorViewCoordinator = LocationErrorViewCoordinator(component)

        @Provides
        @LocationErrorViewScope
        internal fun provideLocationErrorInteractor(locationUpdater: LocationUpdater): LocationErrorViewInteractorImpl =
            LocationErrorViewInteractorImpl(locationUpdater)

        @Provides
        @LocationErrorViewScope
        internal fun provideInteractor(interactor: LocationErrorViewInteractorImpl): LocationErrorViewInteractor =
            interactor

        @Provides
        @LocationErrorViewScope
        internal fun provideViewHolder(interactor: LocationErrorViewInteractor): LocationErrorViewHolder =
            LocationErrorViewHolder(container, interactor)
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LocationErrorViewScope
