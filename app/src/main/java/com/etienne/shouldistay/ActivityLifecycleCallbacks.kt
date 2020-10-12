package com.etienne.shouldistay

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject

@ApplicationContext
class ActivityLifecycleCallbacks
@Inject constructor() : Application.ActivityLifecycleCallbacks {

    private var runningActivities = 0

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        runningActivities++
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }


    override fun onActivityStopped(activity: Activity) {
        runningActivities--
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    fun isRootActivity(): Boolean = runningActivities <= 1
}
