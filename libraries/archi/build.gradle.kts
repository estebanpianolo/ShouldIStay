plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

dependencies {
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.appCompat)
    implementation(Dependencies.material)

    implementation(Dependencies.Rx.java)
    implementation(Dependencies.Rx.android)
    implementation(Dependencies.Rx.kotlin)

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(project(":libraries:pratik"))
}
