plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "pe.edu.uni.adaptivestudy"
    compileSdk = 35

    defaultConfig {
        applicationId = "pe.edu.uni.adaptivestudy"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
