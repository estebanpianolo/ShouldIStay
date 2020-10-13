package com.etienne.shouldistay

import android.app.Application
import android.content.Context
import com.etienne.libraries.network.NetworkConnector
import com.etienne.shouldistay.data.PlayServicesLocationRepository
import com.etienne.shouldistay.domain.LocationRepository
import com.etienne.shouldistay.domain.LocationRetriever
import com.etienne.shouldistay.domain.LocationRetrieverImpl
import com.etienne.shouldistay.domain.LocationUpdater
import com.google.gson.TypeAdapter
import com.google.gson.typeadapters.UtcDateTypeAdapter
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
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

    @Provides
    @ApplicationScope
    fun provideLocationRetriever(locationRepository: LocationRepository): LocationRetriever =
        LocationRetrieverImpl(locationRepository)

    @Provides
    @ApplicationScope
    fun provideLocationUpdater(locationRetriever: LocationRetriever): LocationUpdater =
        locationRetriever

    @Provides
    @ApplicationScope
    fun provideLocationRepository(): LocationRepository =
        PlayServicesLocationRepository(context)

    @Provides
    @ApplicationScope
    internal fun provideUtcDateAdapter(): TypeAdapter<Date> = UtcDateTypeAdapter()

    @Provides
    @ApplicationScope
    fun provideNetworkConnector(utcDateAdapter: TypeAdapter<Date>): NetworkConnector =
        NetworkConnector.createNewConnector(
            context.getString(R.string.clima_cell_url),
            listOf("apikey" to context.getString(R.string.clima_cell_key)),
            listOf(Date::class.java to utcDateAdapter)
        )

}

@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
