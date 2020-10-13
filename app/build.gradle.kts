plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("kotlin-android-extensions")
}

android {
  compileSdkVersion(Values.androidCompileSDK)
  buildToolsVersion(Values.androidBuildTools)

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    applicationId = "com.etienne.shouldistay"

    versionCode = Values.androidVersionCode
    versionName = Values.androidVersionName
    minSdkVersion(Values.androidMinSDK)
    targetSdkVersion(Values.androidTargetSDK)

    vectorDrawables.useSupportLibrary = true
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
    getByName("debug") {
      isDebuggable = true
      applicationIdSuffix = ".debug"
    }
  }

  packagingOptions {
    exclude("META-INF/services/javax.annotation.processing.Processor")
    exclude("META-INF/LICENSE")
    exclude("META-INF/NOTICE")
    exclude("LICENSE.txt")
    exclude("META-INF/rxjava.properties")
    exclude("project.clj")
  }

  flavorDimensions("default")
}

dependencies {
  implementation(Dependencies.stdLib)
  implementation(Dependencies.coreKtx)
  implementation(Dependencies.appCompat)
  implementation(Dependencies.material)
  implementation(Dependencies.constraintLayout)

  implementation(Dependencies.PlayServices.location)

  implementation(Dependencies.Rx.java)
  implementation(Dependencies.Rx.android)
  implementation(Dependencies.Rx.kotlin)

  kapt(Dependencies.Dagger.compiler)
  implementation(Dependencies.Dagger.dagger)
  implementation(Dependencies.Dagger.android)

  implementation(Dependencies.RxBinding.base)
  implementation(Dependencies.RxBinding.appCompat)

  implementation(Dependencies.Retrofit.retrofit)
  implementation(Dependencies.Gson.core)
  implementation(Dependencies.Gson.extras)

  implementation(project(":libraries:archi"))
  implementation(project(":libraries:pratik"))
  implementation(project(":libraries:network"))

  testImplementation("junit:junit:4.13")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
