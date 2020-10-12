package com.etienne.libraries.pratik.rx

import io.reactivex.rxjava3.disposables.Disposable

/**
 * This method disposes the Disposable? receiver if it is not disposed
 */
fun Disposable?.dispose() = this?.let {
    if (!it.isDisposed) it.dispose()
}
