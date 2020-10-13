package com.etienne.shouldistay.domain

import com.etienne.libraries.archi.nucleus.Action
import com.etienne.libraries.archi.nucleus.Command
import io.reactivex.rxjava3.core.Single

class RetrieveCurrentLocationCommand(private val locationRepository: LocationRepository) : Command {
    override fun execute(): Single<Action> =
        locationRepository.getLastKnownLocation()
            .map {
                if (it.isPresent) {
                    LocationRetriever.UpdateLocation(it.get())
                } else {
                    LocationRetriever.UnknownUpdateLocationError
                }
            }.onErrorReturn { LocationRetriever.UpdateLocationError(it) } as Single<Action>
}
