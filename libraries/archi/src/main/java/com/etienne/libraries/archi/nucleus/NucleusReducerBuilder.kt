package com.etienne.libraries.archi.nucleus

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

typealias NucleusReduceFun<S, A> = S.(A) -> Pair<S, Command>

@Suppress("UNCHECKED_CAST")
class NucleusReducerBuilder<S> {

    private val predicates = mutableMapOf<KClass<*>, NucleusReduceFun<S, Action>>()

    fun build(): NucleusReducer<S> = { (state, _), action ->
        val actionKlass = action.javaClass.kotlin
        (predicates[actionKlass]
                ?: predicates.filterKeys { actionKlass.isSubclassOf(it) }.values.firstOrNull()
                )
            ?.invoke(state, action) ?: Pair(state, NoCommand)
    }

    fun <A : Action> registerStateChange(
        kClass: KClass<A>,
        reduce: S.(A) -> S
    ): NucleusReducerBuilder<S> = apply { kClass changesState reduce }

    fun <A : Action> registerCommandChange(
        kClass: KClass<A>,
        reduce: S.(A) -> Command
    ): NucleusReducerBuilder<S> = apply { kClass changesCommand reduce }

    fun <A : Action> registerBothChange(
        kClass: KClass<A>,
        reduce: S.(A) -> Pair<S, Command>
    ): NucleusReducerBuilder<S> = apply { kClass changesBoth reduce }

    infix fun <A : Action> KClass<A>.changesState(reduce: S.(A) -> S) = addReducer(
        { state: S, action: A -> Pair(reduce(state, action), NoCommand) } as NucleusReduceFun<S, A>
    )

    infix fun <A : Action> KClass<A>.changesCommand(reduce: S.(A) -> Command) = addReducer(
        { state: S, action: A -> Pair(state, reduce(state, action)) } as NucleusReduceFun<S, A>
    )

    infix fun <A : Action> KClass<A>.executesSimpleCommand(function: () -> Unit) = addReducer(
        { state: S, _: A -> Pair(state, simpleCommand(function = function)) } as NucleusReduceFun<S, A>
    )

    infix fun <A : Action> KClass<A>.changesBoth(reduce: S.(A) -> Pair<S, Command>) =
        addReducer(reduce)

    private fun <A : Action> KClass<A>.addReducer(reduce: S.(A) -> Pair<S, Command>) {
        if (predicates[this] == null) {
            predicates[this] = reduce as NucleusReduceFun<S, Action>
        } else {
            throw DuplicatedActionException(this)
        }
    }

    class DuplicatedActionException(action: KClass<*>) :
        IllegalStateException("Action $action already present in the reducer")
}
