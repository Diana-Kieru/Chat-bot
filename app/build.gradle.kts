import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.chatgptchatbot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatgptchatbot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"AIzaSyCIhtibrwKcCbcahpCN7rk2h0_rXMw0IHc\"")

    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")

    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:32.0.1-android")

    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")


}