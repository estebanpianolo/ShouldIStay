package com.etienne.shouldistay

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Scope


@Module
class AppModule(application: Application) {

    private val context: Context = application.applicationContext

    @Provides
    @ApplicationScope
    @ApplicationContext
    internal fun provideApplicationContext(): Context = context

    @ApplicationScope
    @IOScheduler
    @Provides
    fun provideRxIoScheduler(): Scheduler = Schedulers.io()

    @ApplicationScope
    @MainScheduler
    @Provides
    fun provideRxMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @ApplicationScope
    @ComputationScheduler
    @Provides
    fun provideRxComputationScheduler(): Scheduler = Schedulers.computation()

    @Provides
    @ApplicationScope
    fun provideAppInjector(appComponent: AppComponent): AppInjector = AppInjectorImpl(appComponent)
}

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
