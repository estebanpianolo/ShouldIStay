package com.etienne.libraries.archi.nucleus

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.subjects.PublishSubject

typealias NucleusReducerConfigurator<S> = NucleusReducerBuilder<S>.() -> Unit

abstract class NucleusInteractorImpl<S>(private val initialObservable: Observable<S>) :
    ClearableInteractor,
    NucleusInteractor<S> {
    constructor(initialState: S) : this(Observable.just(initialState))

    override val actions: PublishSubject<Action> = PublishSubject.create()

    abstract val reducerConfigurator: NucleusReducerConfigurator<S>
    open val reducerHook: NucleusReducerHook<S> = { _, _, _, _, _ -> }

    private val nucleus: Nucleus<S> by lazy {
        Nucleus(
            initialObservable,
            actions,
            NucleusReducerBuilder<S>()
                .apply { reducerConfigurator() }
                .build(),
            reducerHook
        )
    }

    override val state: Observable<S> by lazy { nucleus.state }

    override fun clear() {
        nucleus.clear()
    }
}

interface ClearableInteractor {
    fun clear()
}

interface NucleusInteractor<S> : NucleusActions, NucleusState<S>

interface NucleusActions {
    val actions: Observer<Action>
}

interface NucleusState<S> {
    val state: Observable<S>
}
