package com.etienne.shouldistay.main.locationerror.domain

import com.etienne.libraries.archi.nucleus.*
import com.etienne.shouldistay.domain.LocationUpdater

class LocationErrorViewInteractorImpl(locationUpdater: LocationUpdater) : NucleusInteractorImpl<Unit>(Unit),
    LocationErrorViewInteractor {

    override val reducerConfigurator: NucleusReducerConfigurator<Unit> = {
        LocationErrorViewInteractor.UpdateLocationPermissionTaped::class changesCommand {
            simpleCommand {
                locationUpdater.update()
            }
        }
    }
}

interface LocationErrorViewInteractor : NucleusInteractor<Unit>, ClearableInteractor {
    object UpdateLocationPermissionTaped : Action
}
