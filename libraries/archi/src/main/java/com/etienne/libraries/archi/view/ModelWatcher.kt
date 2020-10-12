package com.etienne.libraries.archi.view

typealias Compare<T> = (T, T) -> Boolean

class ModelWatcher<S> private constructor(private val watchers: MutableList<Watcher<S, Any?>>) {

    private var state: S? = null

    operator fun invoke(newState: S) {
        val oldState = state
        watchers.forEach {
            val mapper = it.mapper
            val property = mapper(newState)
            if (oldState == null || !compare(it.compare, mapper(oldState), property)) {
                it.renderer(property)
            }
        }
        state = newState
    }

    private fun compare(compare: Compare<Any?>?, oldValue: Any?, newValue: Any?) =
        compare?.invoke(oldValue, newValue) ?: oldValue == newValue

    @Suppress("UNCHECKED_CAST")
    class Builder<S> {

        private val watchers = mutableListOf<Watcher<S, Any?>>()

        internal fun build(): ModelWatcher<S> = ModelWatcher(watchers)

        fun watch(compare: Compare<S>? = null, renderer: (S) -> Unit) {
            watch({ this }, compare, renderer)
        }

        fun <P> watch(
            property: S.() -> P,
            compare: Compare<P>? = null,
            renderer: (P) -> Unit
        ) {
            watchers.add(Watcher(property, compare, renderer) as Watcher<S, Any?>)
        }

        infix operator fun <P> (S.() -> P).invoke(renderer: (P) -> Unit) {
            watch(property = this, renderer = renderer)
        }

        operator fun <P> (Compare<P>).invoke(renderer: (P) -> Unit) = this to renderer

        infix fun <P> (S.() -> P).comparing(pair: Pair<Compare<P>, (P) -> Unit>) {
            watch(this, pair.first, pair.second)
        }
    }

    private data class Watcher<S, P>(
        val mapper: S.() -> P,
        val compare: Compare<P>?,
        val renderer: (P) -> Unit
    )

}
