package com.etienne.libraries.pratik.tools

import android.app.Activity


fun Activity.requestPermission(
    requestCode: Int,
    neededPermission: String
) {
    if (androidx.core.content.ContextCompat.checkSelfPermission(
            this,
            neededPermission
        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        androidx.core.app.ActivityCompat.requestPermissions(
            this, arrayOf(neededPermission),
            requestCode
        )
    }
}
