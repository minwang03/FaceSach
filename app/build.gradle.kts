plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.facesach"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.facesach"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.gsonconverter)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation("com.stripe:stripe-android:20.44.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("io.socket:socket.io-client:2.0.1")

}