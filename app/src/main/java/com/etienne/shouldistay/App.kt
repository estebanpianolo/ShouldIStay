package com.etienne.shouldistay

import com.etienne.libraries.archi.view.Application
import com.etienne.shouldistay.main.presentation.MainActivity

import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject


class App : Application() {

    @Inject
    lateinit var activityLifecycleCallbacks: com.etienne.shouldistay.ActivityLifecycleCallbacks

    @Inject
    lateinit var injector: AppInjector

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>

    val appComponent: AppComponent
        get() = injector.appComponent

    val isRootActivity: Boolean
        get() = activityLifecycleCallbacks.isRootActivity()

    override val isAppInitialized: () -> Boolean = { MainActivity.isInitialized }

    override fun buildAppDependencies() {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
            .inject(this)
    }
}
