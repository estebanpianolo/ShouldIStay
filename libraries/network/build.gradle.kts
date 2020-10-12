plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

dependencies {
    implementation(Dependencies.Rx.java)
    implementation(Dependencies.Rx.android)
    implementation(Dependencies.Rx.kotlin)

    implementation(Dependencies.Retrofit.retrofit)
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation(Dependencies.Retrofit.jsonConverter)

    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
