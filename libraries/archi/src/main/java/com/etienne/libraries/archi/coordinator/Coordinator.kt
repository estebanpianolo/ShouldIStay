package com.etienne.libraries.archi.coordinator

import android.content.Intent
import java.util.*
import kotlin.reflect.KClass

/**
 * @param component provides the feature dependencies. This component is retained
 * by this coordinator and is used to inject the coordinator itself.
 */
abstract class Coordinator<C>(protected val component: C) {

    private val childrenStack: Deque<Coordinator<*>> = ArrayDeque()

    abstract fun start()

    abstract fun onRelease()

    /**
     * This method dispatches the back event to its last child to know if it handles the back event.
     * If not, the method checks if this coordinator handles the event.
     * If not, it returns False and the parent coordinator will try to handle it.
     *
     * @return Boolean : True if this method handled the back event otherwise False
     */
    fun backPressed(): Boolean {
        val activeChild = childrenStack.peek()?.backPressed() ?: false
        if (!activeChild)
            return ((this is BackPressedListener) && onBackPressed())
        return activeChild
    }

    /**
     * This method dispatches onActivityResult to its last child to know if it handles the event.
     * If not, the method checks if this coordinator handles the event.
     * If not, it returns False and the parent coordinator will try to handle it.
     *
     * @return Boolean : True if this method handled the event otherwise False
     */
    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val activeChild =
            childrenStack.peek()?.activityResult(requestCode, resultCode, data) ?: false
        if (!activeChild)
            return ((this is ActivityResultHandler) && onActivityResult(
                requestCode,
                resultCode,
                data
            ))
        return activeChild
    }

    /**
     * Will release this coordinator and recursively all the children attached to this coordinator
     */
    protected fun release() {
        childrenStack.forEach { it.release() }
        childrenStack.clear()
        onRelease()
    }

    /**
     * Attach the coordinator to the children stack. It allows us to remember which coordinator is
     * currently instantiated and to manage navigation between coordinators.
     *
     * @param coordinator : The coordinator instance to add in our stack
     */
    protected fun attachCoordinator(coordinator: Coordinator<*>) {
        childrenStack.push(coordinator)
    }

    /**
     * Detach a coordinator previously added to the children stack. It will call the release method
     * to free child coordinator and remove it from the children stack.
     *
     * @throws IllegalStateException when the coordinator has not been added to the children stack
     */
    protected fun detachCoordinator(coordinator: KClass<out Coordinator<*>>) {
        val coordinatorInstance = findFromKClass(coordinator)
        coordinatorInstance?.let {
            it.release()
            childrenStack.removeFirstOccurrence(it)
        }
    }


    fun <T : Coordinator<*>> findChildCoordinator(type: KClass<T>): T =
        findFromKClass(type)
            ?: throw IllegalStateException("Required coordinator is not a child of this coordinator")

    fun <T : Coordinator<*>> isCoordinatorAttached(type: KClass<T>): Boolean =
        findFromKClass(type) != null

    @Suppress("UNCHECKED_CAST")
    private fun <T : Coordinator<*>> findFromKClass(type: KClass<T>): T? {
        return childrenStack.find { it::class == type } as T?
    }

    fun attachTo(coordinator: Coordinator<*>) {
        coordinator.attachCoordinator(this)
    }


}

/**
 * This interface must be implemented by coordinator that wants to handle back event to
 * dismiss there children coordinators.
 */
interface BackPressedListener {
    /**
     * This method must detach children coordinator when needed and return True when
     * this method did something or False if nothing has to be done by the coordinator
     * (basically it means all children coordinators are already detached)
     *
     * @return True if this method handled the back event otherwise False
     */
    fun onBackPressed(): Boolean
}

/**
 * This interface must be implemented by coordinator that wants to handle onActivityResult.
 */
interface ActivityResultHandler {
    /**
     * This method must return True whe this method did something or False if nothing
     * has to be done by the coordinator (basically it means all children coordinators are already detached)
     *
     * @return True if this method handled the back event otherwise False
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}

