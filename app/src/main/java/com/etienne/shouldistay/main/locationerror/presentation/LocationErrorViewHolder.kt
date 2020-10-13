package com.etienne.shouldistay.main.locationerror.presentation

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.etienne.libraries.archi.nucleus.NucleusInteractor
import com.etienne.libraries.archi.view.Dispatcher
import com.etienne.libraries.archi.view.NucleusViewHolder
import com.etienne.libraries.archi.view.dispatch
import com.etienne.shouldistay.R
import com.etienne.shouldistay.main.locationerror.domain.LocationErrorViewInteractor
import com.jakewharton.rxbinding4.view.clicks

class LocationErrorViewHolder(rootView: ViewGroup, interactor: NucleusInteractor<Unit>) :
    NucleusViewHolder<ConstraintLayout, Unit>(rootView, R.layout.view_locationerror, interactor) {

    override val dispatcher: Dispatcher = dispatch(
        findViewById<View>(R.id.location_error_button_title).clicks()
            .map { LocationErrorViewInteractor.UpdateLocationPermissionTaped }
    )
}
