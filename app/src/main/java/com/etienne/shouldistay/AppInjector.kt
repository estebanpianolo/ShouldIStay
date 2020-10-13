package com.etienne.shouldistay

import android.view.ViewGroup
import com.etienne.shouldistay.main.MainComponent
import com.etienne.shouldistay.main.presentation.MainActivity

class AppInjectorImpl(override val appComponent: AppComponent) : AppInjector {

    //region MainActivityScope Component
    private var mainComponent: MainComponent? = null

    override fun createMainComponent(parent: ViewGroup, mainActivity: MainActivity): MainComponent =
        appComponent.plus(MainComponent.Module(parent, mainActivity))
            .apply {
                mainComponent = this
            }

    override fun releaseMainComponent() {
        mainComponent = null
    }
    //endRegion

}

interface AppInjector {
    fun createMainComponent(parent: ViewGroup, mainActivity: MainActivity): MainComponent
    fun releaseMainComponent()

    val appComponent: AppComponent
}
