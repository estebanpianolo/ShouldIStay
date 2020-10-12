object Values {

    const val androidMinSDK = 21
    const val androidCompileSDK = 29
    const val androidTargetSDK = 29
    const val androidVersionCode = 1
    const val androidVersionName = "0.0.1"
    const val androidBuildTools = "29.0.2"

    const val kotlin = "1.4.10"
}

private object DependencyVersions {
    object AndroidX {
        const val appCompat = "1.2.0"
        const val coreKtx = "1.3.1"
        const val constraintLayout = "1.1.3"
        const val material = "1.2.0"
        const val recyclerView = "1.2.0"
    }

    const val rx = "3.0.0"

    const val dagger = "2.29.1"

    const val rxBinding = "4.0.0"

    object Retrofit {
        const val retrofit = "2.9.0"
        const val rxAdapter = "3.0.6"
        const val jsonConverter = "2.9.0"
    }

    const val gson = "2.8.6"
}

object Dependencies {

    const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Values.kotlin}"

    const val coreKtx = "androidx.core:core-ktx:${DependencyVersions.AndroidX.coreKtx}"
    const val appCompat = "androidx.appcompat:appcompat:${DependencyVersions.AndroidX.appCompat}"
    const val material =
        "com.google.android.material:material:${DependencyVersions.AndroidX.material}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${DependencyVersions.AndroidX.constraintLayout}"

    object Rx {
        const val java = "io.reactivex.rxjava3:rxjava:${DependencyVersions.rx}"
        const val android = "io.reactivex.rxjava3:rxandroid:${DependencyVersions.rx}"
        const val kotlin = "io.reactivex.rxjava3:rxkotlin:${DependencyVersions.rx}"
    }

    object Dagger {
        const val compiler = "com.google.dagger:dagger-compiler:${DependencyVersions.dagger}"
        const val dagger = "com.google.dagger:dagger:${DependencyVersions.dagger}"
        const val android = "com.google.dagger:dagger-android:${DependencyVersions.dagger}"
    }

    object RxBinding {
        const val base = "com.jakewharton.rxbinding4:rxbinding:${DependencyVersions.rxBinding}"
        const val appCompat =
            "com.jakewharton.rxbinding4:rxbinding-appcompat:${DependencyVersions.rxBinding}"
    }

    object Retrofit {
        const val retrofit =
            "com.squareup.retrofit2:retrofit:${DependencyVersions.Retrofit.retrofit}"
        const val rxAdapter =
            "com.github.akarnokd:rxjava3-retrofit-adapter:${DependencyVersions.Retrofit.rxAdapter}"
        const val jsonConverter =
            "com.squareup.retrofit2:converter-gson:${DependencyVersions.Retrofit.jsonConverter}"
    }

    const val gson = "com.google.code.gson:gson:${DependencyVersions.gson}"

}

