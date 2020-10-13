package com.etienne.shouldistay.main.locationerror.presentation

import com.etienne.libraries.archi.coordinator.Coordinator
import com.etienne.shouldistay.main.locationerror.LocationErrorViewComponent

class LocationErrorViewCoordinator(component: LocationErrorViewComponent) : Coordinator<LocationErrorViewComponent>(component){
    override fun start() {
        component.viewHolder().addOnRootView()
    }

    override fun onRelease() {
        component.viewHolder().removeFromRootView()
        component.interactor().clear()
    }
}
