package com.etienne.libraries.archi.nucleus

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.observables.ConnectableObservable
import io.reactivex.rxjava3.subjects.PublishSubject


typealias NucleusReducer<S> = Nucleus<S>.(Pair<S, Command>, Action) -> Pair<S, Command>

typealias NucleusReducerHook<S> = (previousState: S, previousCommand: Command, action: Action, nextState: S, nextCommand: Command) -> Unit

class Nucleus<S>(
    initialState: Single<S>,
    viewActions: Observable<Action>,
    reducer: NucleusReducer<S>,
    reducerHook: NucleusReducerHook<S> = { _, _, _, _, _ -> },
    autoStart: Boolean = true
) {
    constructor(
        initialState: S,
        viewActions: Observable<Action>,
        reducer: NucleusReducer<S>,
        hookReducer: NucleusReducerHook<S> = { _, _, _, _, _ -> },
        autoStart: Boolean = true
    ) : this(Single.just(initialState), viewActions, reducer, hookReducer)

    constructor(
        initialState: Observable<S>,
        viewActions: Observable<Action>,
        reducer: NucleusReducer<S>,
        hookReducer: NucleusReducerHook<S> = { _, _, _, _, _ -> },
        autoStart: Boolean = true
    ) : this(initialState.firstOrError(), viewActions, reducer, hookReducer, autoStart)

    private var isStarted: Boolean = false

    private val disposables = CompositeDisposable()

    private val cmdActions = PublishSubject.create<Action>()
    private val actions: ConnectableObservable<Action> = Observable
        .merge(viewActions, cmdActions)
        .startWithItem(Initialization)
        .replay()

    private val store: ConnectableObservable<Pair<S, Command>> = initialState
        .flatMapObservable {
            actions
                .filter { action -> action !is DoNothing }
                .scan(Pair(it, NoCommand as Command))
                { (previousState: S, previousCommand: Command), action ->
                    reducer(this, Pair(previousState, previousCommand), action)
                        .also { (nextState, nextCommand) ->
                            reducerHook(
                                previousState,
                                previousCommand,
                                action,
                                nextState,
                                nextCommand
                            )
                        }
                }
        }
        .publish()

    val state: Observable<S> = store
        .map { it.first }
        .distinctUntilChanged()
        .replay(1)
        .autoConnect(0) {
            disposables.add(it)
        }

    init {
        disposables += store
            .map { it.second }
            .filter { it != NoCommand }
            .distinctUntilChanged()
            .flatMapSingle {
                it.execute()
            }
            .subscribe(cmdActions::onNext)

        if (autoStart)
            start()
    }

    fun start() {
        if (isStarted)
            throw AlreadyStartedException()
        disposables += actions.connect()
        disposables += store.connect()
        isStarted = true
    }

    fun clear() {
        disposables.clear()
    }
}

class AlreadyStartedException : IllegalStateException("Start function already called")
