package com.etienne.libraries.pratik.rx

import com.etienne.libraries.pratik.compat.Optional
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

/**
 * @param scheduler on which the Observable<T> will be observed
 * @return the Disposable one can use to cancel the Observable<T> emission
 */
@CheckReturnValue
fun <T> Observable<T>.subscribeWithScheduler(scheduler: Scheduler): Disposable =
    this.observeOn(scheduler).subscribe()

/**
 * @param scheduler on which the Observable<T> will be observed
 * @param observer that will subscribe to the Observable<T>
 * @return the Disposable one can use to cancel the Observable<T> emission
 */
@CheckReturnValue
fun <T> Observable<T>.subscribeWithScheduler(scheduler: Scheduler, observer: Observer<T>) {
    this.observeOn(scheduler).subscribe(observer)
}

/**
 * @param scheduler on which the Observable<T> will be observed
 * @param onNext the lambda run when an event is onNext'd
 * @return the Disposable one can use to cancel the Observable<T> emission
 */
@CheckReturnValue
inline fun <T> Observable<T>.subscribeWithScheduler(
    scheduler: Scheduler,
    crossinline onNext: (T) -> Unit
): Disposable =
    this.observeOn(scheduler).subscribe { onNext(it) }

/**
 * @param scheduler on which the Observable<T> will be observed
 * @param onNext the lambda run when an event is onNext'd
 * @param onError the lambda run when an error occurred
 * @return the Disposable one can use to cancel the Observable<T> emission
 */
@CheckReturnValue
inline fun <T> Observable<T>.subscribeWithScheduler(
    scheduler: Scheduler,
    crossinline onNext: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit
): Disposable =
    this.observeOn(scheduler).subscribe({ onNext(it) }, { onError(it) })

/**
 * @param scheduler on which the Observable<T> will be observed
 * @param onNext the lambda run when an event is onNext'd
 * @param onError the lambda run when an error occurred
 * @param onComplete the lambda run when the stream completes
 * @return the Disposable one can use to cancel the Observable<T> emission
 */
@CheckReturnValue
inline fun <T> Observable<T>.subscribeWithScheduler(
    scheduler: Scheduler,
    crossinline onNext: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline onComplete: () -> Unit
): Disposable =
    this.observeOn(scheduler).subscribe({ onNext(it) }, { onError(it) }, { onComplete() })

/**
 * @param scheduler on which the Single<T> will be observed
 * @param onEvent the lambda run when an event is emitted
 * @return the Disposable one can use to cancel the Single<T> emission
 */
@CheckReturnValue
inline fun <T> Single<T>.subscribeWithScheduler(
    scheduler: Scheduler,
    crossinline onEvent: (T) -> Unit
): Disposable = observeOn(scheduler).subscribe(Consumer<T> { onEvent(it) })


/**
 * This method is a helper to emit received items from the Observable<out O> receiver to an Observer<O>
 * @param observer that will receive the emitted items from receiver
 * @return the Disposable one can use to cancel the items emission from the receiver
 * Nb : the onComplete and onError events are not propagated, i.e. the resulted Observable never completes unless one dispose the returned Disposable
 */
@CheckReturnValue
fun <O> Observable<out O>.relay(observer: Observer<O>): Disposable = subscribe(
    { observer.onNext(it) }, { }, { }
)

/**
 * This method is a helper to emit the received item from the Single<out O> receiver to an Observer<O>
 * @param observer that will receive the emitted item from receiver
 * @return the Disposable one can use to cancel the item emission from the receiver
 * Nb : the onError event is not propagated, i.e. the resulted Observable never completes unless one dispose the returned Disposable
 */
@CheckReturnValue
fun <O> Single<out O>.relay(observer: Observer<O>): Disposable = subscribe(
    { observer.onNext(it) }, { }
)

/**
 * This method is a helper to emit the received item from the Maybe<out O> receiver to an Observer<O>
 * @param observer that will receive the emitted item from receiver
 * @return the Disposable one can use to cancel the item emission from the receiver
 * Nb : the onComplete and onError events are not propagated, i.e. the resulted Observable never
 * completes unless one dispose the returned Disposable
 */
@CheckReturnValue
fun <O> Maybe<out O>.relay(observer: Observer<O>): Disposable = subscribe(
    { observer.onNext(it) }, { }
)

/**
 * @param block the lambda run if the MaybeEmitter<T> receiver is not disposed.
 * Can be used when dealing with Maybe creation :
 * Maybe.create { emitter: MaybeEmitter<String> ->
 *      emitter.ifNotDisposed {
 *          onSuccess("aString")
 *      }
 * }
 */
inline fun <T> MaybeEmitter<T>.ifNotDisposed(block: MaybeEmitter<T>.() -> Unit) {
    if (!this.isDisposed) {
        block.invoke(this)
    }
}

/**
 * @param block the lambda run if the ObservableEmitter<T> receiver is not disposed.
 * Can be used when dealing with Observable creation :
 * Observable.create { emitter: ObservableEmitter<String> ->
 *      emitter.ifNotDisposed {
 *          onNext("aString")
 *          onNext("anotherString")
 *          onComplete()
 *      }
 * }
 */
inline fun <T> ObservableEmitter<T>.ifNotDisposed(block: ObservableEmitter<T>.() -> Unit) {
    if (!this.isDisposed) {
        block.invoke(this)
    }
}

/**
 * @param block the lambda run if the CompletableEmitter<T> receiver is not disposed.
 * Can be used when dealing with Completable creation :
 * Completable.create { emitter: CompletableEmitter ->
 *      emitter.ifNotDisposed {
 *          onComplete()
 *      }
 * }
 */
inline fun CompletableEmitter.ifNotDisposed(block: CompletableEmitter.() -> Unit) {
    if (!this.isDisposed) {
        block.invoke(this)
    }
}

/**
 * @param block the lambda run if the SingleEmitter<T> receiver is not disposed.
 * Can be used when dealing with Single creation :
 * Single.create { emitter: SingleEmitter<String> ->
 *      emitter.ifNotDisposed {
 *          onSuccess("aString")
 *      }
 * }
 */
inline fun <T> SingleEmitter<T>.ifNotDisposed(block: SingleEmitter<T>.() -> Unit) {
    if (!this.isDisposed) {
        block.invoke(this)
    }
}

/**
 * This method creates a new Observable<T> that emits the last item of the Observable<T>
 * in parameter when one item is emitted by the Observable<*> receiver
 * @param observable which last emitted item will be emitted in the newly created Observable<T>
 * @return the newly created Observable<T>
 */
@CheckReturnValue
fun <T> Observable<*>.replaceWithLatestFrom(observable: Observable<T>): Observable<T> =
    withLatestFrom(observable) { _, value -> value }

/**
 * This method creates a new Observable<T> that emits the item of the Single<T>
 * in parameter when one item is emitted by the Observable<*> receiver
 * @param single which emitted item will be emitted in the newly created Observable<T>
 * @return the newly created Observable<T>
 * Nb: Even if the Single<T> has already emitted its item, each time the receiver emits an item,
 * the newly created Observable<T> emits the item of the Single<T>
 */
@CheckReturnValue
fun <T> Observable<*>.replaceWithLatestFrom(single: Single<T>) =
    withLatestFrom(single.toObservable()) { _, value -> value }

/**
 * This method creates a new Observable<T> that emits the item of the Maybe<T>
 * in parameter when one item is emitted by the Observable<*> receiver
 * @param maybe which emitted item will be emitted in the newly created Observable<T>
 * @return the newly created Observable<T>
 * Nb: Even if the Maybe<T> has already emitted its item, each time the receiver emits an item,
 * the newly created Observable<T> emits the item of the Single<T>
 */
@CheckReturnValue
fun <T> Observable<*>.replaceWithLatestFrom(maybe: Maybe<T>) =
    withLatestFrom(maybe.toObservable()) { _, value -> value }

/**
 * This method creates a new Observable<T> that emits mapped items from the Observable<Optional<T>>
 * receiver if the Optional<T> item contains a non empty value
 * @param mapper the lambda to mapper the T emitted item to a new R item
 * @return the newly created Observable<R>
 */
@CheckReturnValue
inline fun <T, R> Observable<Optional<T>>.filterIfPresent(crossinline mapper: (T) -> R): Observable<R> =
    filter { it.isPresent }.map { mapper.invoke(it.get()) }

/**
 * This method creates a new Observable<T> that emits items from the Observable<Optional<T>>
 * receiver if the Optional<T> item contains a non empty value
 * @return the newly created Observable<T>
 */
@CheckReturnValue
fun <T> Observable<Optional<T>>.filterIfPresent(): Observable<T> =
    filter { it.isPresent }.map { it.get() }

/**
 * This method creates a new Observable<O> that emits items from the Observable<T>
 * receiver filtering each item by casting it with a specific type
 * @param clazz that will be used to filter the emitted items by casting them
 * @return the newly created Observable<O>
 */
inline fun <T, reified O : T> Observable<T>.filterByCasting(clazz: Class<O>): Observable<O> =
    filter { it is O }.cast(clazz)

/**
 * This method creates a new Observable<M> that emits mapped items from the Observable<T>
 * receiver filtering each item by casting it with a specific type
 * @param clazz that will be used to filter the emitted items by casting them
 * @param mapper the lambda to map each O emitted item to a new M item
 * @return the newly created Observable<O>
 */
inline fun <T, reified O : T, M> Observable<T>.filterByCasting(
    clazz: Class<O>,
    crossinline mapper: (O) -> M
): Observable<M> = filterByCasting(clazz).map { mapper.invoke(it) }

/**
 * This method creates a new Observable<N> that emits mapped items from the Observable<T>
 * receiver filtering each item by applying a predicate
 * @param predicate the lambda to map each T emitted item to a new M? item.
 * Only non null item will pass the filter
 * @return the newly created Observable<N>
 */
inline fun <T, M> Observable<T>.filterNotNull(crossinline predicate: (T) -> M?): Observable<M> =
    filter { predicate.invoke(it) != null }.map { predicate.invoke(it) }

/**
 * This method creates a new Observable<N> that emits mapped items from the Observable<T>
 * receiver filtering each item by applying a predicate
 * @param predicate the lambda to map each T emitted item to a new M? item.
 * Only non null item will pass the filter
 * @param mapper the lambda to map each M emitted item to a new N item
 * @return the newly created Observable<N>
 */
inline fun <T, M, N> Observable<T>.filterNotNull(
    crossinline predicate: (T) -> M?,
    crossinline mapper: (M) -> N
): Observable<N> = filterNotNull(predicate).map { mapper.invoke(it) }

/**
 * This method creates a new Observable<M> that emits mapped items from the Observable<T>
 * receiver filtering each item by applying a predicate
 * @param predicate the lambda that filters the incoming items.
 * @param mapper the lambda to map each T emitted item to a new M item
 * @return the newly created Observable<M>
 */
inline fun <T, M> Observable<T>.filter(
    crossinline predicate: (T) -> Boolean,
    crossinline mapper: (T) -> M
): Observable<M> = filter { predicate.invoke(it) }.map { mapper.invoke(it) }
