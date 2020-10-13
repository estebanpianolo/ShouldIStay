package com.etienne.shouldistay.domain

data class LocationConfig(
    val priority: Int,
    val interval: Int,
    val fastestInterval: Int,
    val smallestDisplacement: Float = 0f
) {
    companion object {
        val BALANCED_POWER_ACCURACY = 1
        val HIGH_ACCURACY = 2
        val LOW_POWER = 3
        val NO_POWER = 4

        @JvmStatic
        fun highAccuracyInstance() = LocationConfig(HIGH_ACCURACY, 10000, 5000, 1.0f)
    }
}
