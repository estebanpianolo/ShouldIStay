package com.etienne.libraries.pratik.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * This method creates a new Observable<T> that directly subscribes to the Observable<T> receiver,
 * keeping some last emitted items
 * @param bufferSize the number of items to emits at most when an observer subscribes to the
 * newly created Observable<T>
 * @return the newly created Observable<T>
 */
fun <T> Observable<T>.shareReplay(compositeDisposable: CompositeDisposable, bufferSize: Int = 1):
    Observable<T> = replay(bufferSize).autoConnect(0) { compositeDisposable.addAll(it) }

fun <T> Observable<T>.shareReplay(connection: (Disposable) -> Unit, bufferSize: Int = 1):
    Observable<T> = replay(bufferSize).autoConnect(0, connection)

