package com.etienne.libraries.archi.view

import android.app.Application
import android.content.Intent

abstract class Application : Application() {

    var appInitialized: Boolean = false
        private set
        get() = isAppInitialized.invoke()

    override fun onCreate() {
        super.onCreate()
        buildAppDependencies()
    }

    abstract val isAppInitialized: () -> Boolean

    protected open fun buildAppDependencies() {}

    open fun intentOnRestartAfterSystemKill(): Intent? = packageManager.getLaunchIntentForPackage(packageName)
}
