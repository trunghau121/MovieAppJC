@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.core_app"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    api(libs.androidx.ktx)
    api(libs.kotlinx.serialization.json)

    api(libs.activity.compose)
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.material3)
    api(libs.lifecycle.compose)
    api(libs.constraintlayout.compose)
    api(libs.foundation.android)
    api(libs.icons.extended)

    api(libs.datastore)
    api(libs.datastore.preferences)
    api(libs.androidx.security.crypto.ktx) {
        exclude(group = "com.google.crypto.tink", module = "tink-android")
    }
    implementation(libs.tink.android)

    api(libs.coil.compose)
    api(libs.coil.transformations)

    api(libs.okhttp3)
    api(libs.okhttp3.logging.interceptor)

    api(libs.moshi.kotlin)
    api(libs.retrofit)
    api(libs.converter.moshi)

    api(libs.coroutines.core)
    api(libs.coroutines.android)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.runtime)

    api(libs.navigation.compose)

    api(libs.timber)

    // Room
    api(libs.room.runtime)
    api(libs.room.ktx)
    api(libs.androidx.fragment.ktx)

}