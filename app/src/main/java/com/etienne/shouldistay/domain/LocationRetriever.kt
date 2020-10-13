package com.etienne.shouldistay.domain

import com.etienne.libraries.archi.nucleus.Action
import com.etienne.libraries.archi.nucleus.NucleusInteractor
import com.etienne.libraries.archi.nucleus.NucleusInteractorImpl
import com.etienne.libraries.archi.nucleus.NucleusReducerConfigurator
import com.etienne.libraries.pratik.compat.Optional


class LocationRetrieverImpl(private val locationRepository: LocationRepository) :
    NucleusInteractorImpl<LocationRetriever.State>(LocationRetriever.State.UnknownLocation),
    LocationRetriever {


    override val reducerConfigurator: NucleusReducerConfigurator<LocationRetriever.State> = {
        RetrieveANewLocation::class changesBoth {
            Pair(
                LocationRetriever.State.UpdatingLocation,
                RetrieveCurrentLocationCommand(locationRepository)
            )
        }
        LocationRetriever.UpdateLocation::class changesState {
            LocationRetriever.State.CurrentLocation(it.location)
        }
        LocationRetriever.UnknownUpdateLocationError::class changesState {
            LocationRetriever.State.Error(Optional.empty())
        }
        LocationRetriever.UpdateLocationError::class changesState {
            LocationRetriever.State.Error(Optional.of(it.throwable))
        }
    }

    override fun update() {
        actions.onNext(RetrieveANewLocation)
    }

    object RetrieveANewLocation : Action
}

interface LocationRetriever : NucleusInteractor<LocationRetriever.State>, LocationUpdater {

    sealed class State {
        object UnknownLocation : State()
        object UpdatingLocation : State()
        data class CurrentLocation(val location: Location) : State()
        data class Error(val throwable: Optional<Throwable>) : State()
    }

    data class UpdateLocation(val location: Location) : Action
    object UnknownUpdateLocationError : Action
    data class UpdateLocationError(val throwable: Throwable) : Action
}

interface LocationUpdater {
    fun update()
}
