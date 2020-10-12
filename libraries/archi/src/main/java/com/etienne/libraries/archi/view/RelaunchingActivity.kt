package com.etienne.libraries.archi.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class RelaunchingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as? Application)?.apply {
            if (!appInitialized) {
                intentOnRestartAfterSystemKill()
                    ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    ?.let { baseContext.startActivity(it) }
                    ?: throw IllegalStateException(
                        "Unable to determine default activity for "
                                + packageName
                                + ". Does an activity specify the DEFAULT category in its intent filter?"
                                + " Otherwise specify a non-null one, overriding 'intentOnRestartAfterSystemKill' in ${application::class.java}"
                    )
                Runtime.getRuntime().exit(0)
            }
        }
    }
}
