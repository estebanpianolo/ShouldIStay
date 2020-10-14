package com.etienne.shouldistay.main.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.etienne.libraries.archi.view.RelaunchingActivity
import com.etienne.libraries.pratik.tools.requestPermission
import com.etienne.shouldistay.App
import com.etienne.shouldistay.AppInjector
import com.etienne.shouldistay.R
import com.google.android.gms.common.api.ResolvableApiException
import javax.inject.Inject

class MainActivity : RelaunchingActivity(), LocationPermissionResolver {

    companion object {
        var isInitialized = false
    }

    init {
        isInitialized = true
    }

    @Inject
    lateinit var appInjector: AppInjector

    @Inject
    lateinit var coordinator: MainCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as App).appComponent
            .provideAppInjector()
            .createMainComponent(findViewById(android.R.id.content), this)
            .inject(this)

        coordinator.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        coordinator.requestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!coordinator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            coordinator.release()
            appInjector.releaseMainComponent()
        }
    }

    override fun resolveLocationPermission(
        resolvableApiException: ResolvableApiException,
        requestCode: Int
    ) {
        resolvableApiException.startResolutionForResult(this, requestCode)
    }

    override fun showLocationPermissionDialog(requestCode: Int) {
        requestPermission(
            requestCode,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}
