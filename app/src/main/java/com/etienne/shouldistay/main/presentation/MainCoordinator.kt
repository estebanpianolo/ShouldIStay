package com.etienne.shouldistay.main.presentation

import android.content.Intent
import android.view.ViewGroup
import com.etienne.libraries.archi.coordinator.ActivityRequestPermissionsResultHandler
import com.etienne.libraries.archi.coordinator.ActivityResultHandler
import com.etienne.libraries.archi.coordinator.Coordinator
import com.etienne.libraries.pratik.compat.Optional
import com.etienne.libraries.pratik.rx.filterByCasting
import com.etienne.shouldistay.domain.LocationRetriever
import com.etienne.shouldistay.main.MainComponent
import com.etienne.shouldistay.main.locationerror.LocationErrorViewComponent
import com.etienne.shouldistay.main.locationerror.presentation.LocationErrorViewCoordinator
import com.etienne.shouldistay.main.weather.WeatherViewComponent
import com.google.android.gms.common.api.ResolvableApiException
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

class MainCoordinator(
    private val parent: ViewGroup,
    component: MainComponent,
    private val locationErrorViewBuilder: LocationErrorViewComponent.Builder,
    private val weatherViewBuilder: WeatherViewComponent.Builder,
    private val locationPermissionResolver: LocationPermissionResolver
) : Coordinator<MainComponent>(component), ActivityRequestPermissionsResultHandler,
    ActivityResultHandler {

    private val disposables = CompositeDisposable()
    override fun start() {
        weatherViewBuilder.module(
            WeatherViewComponent.Module(
                parent,
                component.locationRetriever.state.filterByCasting(LocationRetriever.State.CurrentLocation::class.java) { it.location }
            )
        )
            .build()
            .coordinator()
            .apply {
                start()
                this@MainCoordinator.attachCoordinator(this)
            }

        registerForLocationPermissionUpdates()
    }

    private fun registerForLocationPermissionUpdates() {
        component.locationRetriever.state.subscribe {
            when (it) {
                is LocationRetriever.State.Error -> {
                    handleLocationPermissionError(it.throwable)
                }
                else -> {
                    handleLocationPermissionSuccess(it)
                }
            }
        }.addTo(disposables)
        component.locationRetriever.update()
    }

    private fun handleLocationPermissionSuccess(state: LocationRetriever.State) {
        if (state is LocationRetriever.State.CurrentLocation) {
            detachCoordinator(LocationErrorViewCoordinator::class)
        }
    }

    private fun handleLocationPermissionError(throwable: Optional<Throwable>) {
        if (!isCoordinatorAttached(LocationErrorViewCoordinator::class)) {
            locationErrorViewBuilder.module(LocationErrorViewComponent.Module(parent))
                .build()
                .coordinator()
                .apply {
                    start()
                    this@MainCoordinator.attachCoordinator(this)
                }
        }
        throwable.ifPresent {
            if (it is ResolvableApiException) {
                locationPermissionResolver.resolveLocationPermission(
                    it,
                    locationPermissionResolverRequestCode
                )
            } else {
                locationPermissionResolver.showLocationPermissionDialog(
                    locationPermissionResolverRequestCode
                )
            }
        }
    }

    override fun onRelease() {
        disposables.clear()
    }

    companion object {
        const val locationPermissionResolverRequestCode = 101
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean = if (requestCode == locationPermissionResolverRequestCode) {
        component.locationRetriever.update()
        true
    } else {
        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        if (requestCode == locationPermissionResolverRequestCode) {
            component.locationRetriever.update()
            true
        } else {
            false
        }
}

interface LocationPermissionResolver {
    fun resolveLocationPermission(
        resolvableApiException: ResolvableApiException,
        requestCode: Int
    )

    fun showLocationPermissionDialog(requestCode: Int)
}
