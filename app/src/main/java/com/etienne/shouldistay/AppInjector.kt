package com.etienne.shouldistay

class AppInjectorImpl(override val appComponent: AppComponent) : AppInjector {

}

interface AppInjector {

    val appComponent: AppComponent
}
