package com.etienne.shouldistay

import android.content.Context
import dagger.Component
import dagger.android.AndroidInjectionModule
import io.reactivex.rxjava3.core.Scheduler

import javax.inject.Qualifier

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        AndroidInjectionModule::class,
    ]
)
interface AppComponent {

    @get:ApplicationContext
    val applicationContext: Context


    @get:MainScheduler
    val mainScheduler: Scheduler

    @get:IOScheduler
    val ioScheduler: Scheduler

    @get:ComputationScheduler
    val computationScheduler: Scheduler

    fun provideAppInjector(): AppInjector

    fun inject(app: App)
}

@Qualifier
@kotlin.annotation.MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext

@Qualifier
@kotlin.annotation.MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MainScheduler

@Qualifier
@kotlin.annotation.MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class IOScheduler

@Qualifier
@kotlin.annotation.MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ComputationScheduler
