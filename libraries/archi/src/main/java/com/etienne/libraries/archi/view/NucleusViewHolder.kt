package com.etienne.libraries.archi.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.etienne.libraries.archi.nucleus.Action
import com.etienne.libraries.archi.nucleus.NucleusInteractor
import com.etienne.libraries.pratik.rx.relay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

typealias ModelWatcherConfigurator<S> = ModelWatcher.Builder<S>.() -> Unit
typealias Dispatcher = List<Observable<Action>>

fun dispatch(vararg observables: Observable<Action>): Dispatcher = listOf(*observables)

abstract class NucleusViewHolder<V : View, S> : ViewHolder<V> {

    private val tagLog = javaClass.toString()

    private val interactor: NucleusInteractor<S>

    constructor(contentView: V, interactor: NucleusInteractor<S>) :
            super(contentView) {
        this.interactor = interactor
    }

    constructor(context: Context, resId: Int, interactor: NucleusInteractor<S>) :
            super(context, resId) {
        this.interactor = interactor
    }

    constructor(rootView: ViewGroup, resId: Int, interactor: NucleusInteractor<S>) :
            super(rootView, resId) {
        this.interactor = interactor
    }

    protected open val modelWatchConfigurator: ModelWatcherConfigurator<S> = {}

    protected open val dispatcher: Dispatcher = dispatch()

    final override fun CompositeDisposable.startInteractions() {
        val modelWatcher = ModelWatcher.Builder<S>().apply { modelWatchConfigurator(this) }.build()
        interactor.state.observeOn(AndroidSchedulers.mainThread())
            .subscribe { modelWatcher(it) }
            .addTo(this)

        if (dispatcher.isNotEmpty()) {
            Observable
                .mergeArray(*dispatcher.toTypedArray())
                .relay(interactor.actions)
                .addTo(this)
        }
    }
}
