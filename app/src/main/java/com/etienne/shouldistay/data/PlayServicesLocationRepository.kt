package com.etienne.shouldistay.data

import android.annotation.SuppressLint
import android.content.Context
import com.etienne.libraries.pratik.compat.Optional
import com.etienne.libraries.pratik.rx.ifNotDisposed
import com.etienne.shouldistay.domain.Location
import com.etienne.shouldistay.domain.LocationRepository
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter

class PlayServicesLocationRepository(private val context: Context) : LocationRepository {

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(): Single<Optional<Location>> =
        checkLocationSettings(createLocationSettingsRequest())
            .flatMap {
                when (it) {
                    Settings.Enabled -> getLocation()
                    is Settings.Disabled -> Single.error(it.resolvableApiException)
                    is Settings.Unknown -> Single.error(it.exception)
                }
            }

    @SuppressLint("MissingPermission")
    private fun getLocation(): @NonNull Single<Optional<Location>> {
        var shouldBeCancelled = false
        val cancellationToken = object : CancellationToken() {
            override fun isCancellationRequested() = shouldBeCancelled

            override fun onCanceledRequested(p0: OnTokenCanceledListener) = this

        }
        return Single.create { emitter: SingleEmitter<Optional<Location>> ->
            val client = LocationServices.getFusedLocationProviderClient(context)
            try {
                client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken)
                    .addOnSuccessListener { location ->
                        emitter.ifNotDisposed {
                            location?.let { onSuccess(Optional.of(it.model())) }
                                ?: onSuccess(Optional.empty())
                        }
                    }.addOnFailureListener {
                        emitter.ifNotDisposed { onError(it) }
                    }
            } catch (exception: Exception) {
                emitter.ifNotDisposed { onError(exception) }
            }
        }.doOnDispose {
            shouldBeCancelled = true
        }
    }

    private fun checkLocationSettings(request: LocationSettingsRequest) =
        Single.create { emitter: SingleEmitter<Settings> ->
            val client = LocationServices.getSettingsClient(context)
            client.checkLocationSettings(request)
                .addOnSuccessListener { emitter.ifNotDisposed { onSuccess(Settings.Enabled) } }
                .addOnFailureListener { exception ->
                    emitter.ifNotDisposed {
                        onSuccess(
                            when (exception) {
                                is ResolvableApiException ->
                                    Settings.Disabled(exception)
                                else -> Settings.Unknown(exception)
                            }
                        )
                    }
                }
        }

    private fun createLocationSettingsRequest() =
        LocationSettingsRequest.Builder()
            .addLocationRequest(
                LocationRequest().apply {
                    interval = LocationSettingsInterval
                    fastestInterval = LocationSettingsFastestInterval
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    smallestDisplacement = LocationSettingsSmallestDisplacement
                }
            )
            .build()

    companion object {
        private const val LocationSettingsInterval = 10000L
        private const val LocationSettingsFastestInterval = 5000L
        private const val LocationSettingsSmallestDisplacement = 100.0f
    }

    private sealed class Settings {
        object Enabled : Settings()
        data class Disabled(val resolvableApiException: ResolvableApiException) : Settings()
        data class Unknown(val exception: Throwable) : Settings()
    }

}

private fun android.location.Location.model() = Location(latitude, longitude)
