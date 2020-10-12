package com.etienne.libraries.archi.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.ref.WeakReference

abstract class ViewHolder<V : View> {

    private val tagLog
        get() = javaClass.toString()

    val context: Context
        get() = contentView.context

    protected val contentView: V

    val view: View
        get() = contentView

    private var rootView: WeakReference<ViewGroup>?
    private val disposables = CompositeDisposable()

    constructor(contentView: V) {
        this.contentView = contentView
        rootView = null
        initView()
    }

    constructor(context: Context, resId: Int) {
        contentView = inflate(context, resId = resId)
        rootView = null
        initView()
    }

    constructor(rootView: ViewGroup, resId: Int) {
        contentView = inflate(rootView.context, rootView, resId)
        this.rootView = WeakReference(rootView)
        initView()
    }

    private fun inflate(
        context: Context,
        rootView: ViewGroup? = null,
        resId: Int
    ): V {
        val view = LayoutInflater.from(context).inflate(resId, rootView, false)
        try {
            @Suppress("UNCHECKED_CAST")
            return view as V
        } catch (exception: ClassCastException) {
            throw BadLayoutTypeException(view.javaClass)
        }
    }

    private val attachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(p0: View?) {
            buildContentView()
            disposables.startInteractions()
        }

        override fun onViewDetachedFromWindow(p0: View?) {
            disposables.clear()
        }
    }

    private fun initView() {
        contentView.addOnAttachStateChangeListener(attachStateChangeListener)
    }

    fun addOnRootView(rootView: ViewGroup) = apply {
        when (contentView.parent) {
            rootView -> {
            }
            null -> rootView.addView(contentView)
            else -> Log.w(tagLog, "Should not add a view already attached !")
        }
    }

    fun addOnRootView() = apply {
        val rootView = this.rootView?.get()
        if (rootView != null) {
            addOnRootView(rootView)
        } else {
            Log.w(
                tagLog,
                "Called addOnRootView() but rootView is null.\n You should call \"constructor(rootView: ViewGroup, resId: Int)\" to create your viewHolder first "
            )
        }
    }

    fun removeFromRootView() = apply {
        (contentView.parent as? ViewGroup)?.removeView(contentView)
    }

    fun bringToFront(rootView: ViewGroup) = apply {
        when (contentView.parent) {
            rootView -> rootView.bringChildToFront(contentView)
            null -> addOnRootView(rootView)
            else -> {
                Log.w(
                    tagLog,
                    "Should not bringToFront a view already attached to another parent ${contentView.parent}!"
                )
            }
        }
    }

    fun bringToFront() = apply {
        val rootView = this.rootView?.get()
        if (rootView != null) {
            bringToFront(rootView)
        } else {
            Log.w(
                tagLog,
                "Called bringToFront() but rootView is null.\n You should call \"constructor(rootView: ViewGroup, resId: Int)\" to create your viewHolder first "
            )
        }
    }

    protected abstract fun CompositeDisposable.startInteractions()

    protected open fun buildContentView() {}

    class BadLayoutTypeException(foundClass: Class<View>) :
        Exception("Bad layout type found in your xml file\nfoundClass:$foundClass")

    fun holdsViewInstance(view: View) = contentView === view

    protected fun <V : View> findViewById(resId: Int): V = contentView.findViewById(resId) as V

    protected fun <V : View> findOptionalViewById(resId: Int): V? =
        contentView.findViewById(resId) as V?
}
