plugins {
    id("com.android.application")
}

android {
    namespace = "com.centralcampanhas.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.centralcampanhas.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.2"
    }
}

dependencies {
    implementation("androidx.browser:browser:1.8.0")
}
