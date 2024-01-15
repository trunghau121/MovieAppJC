@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.serialization)
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.androidx.ktx)

    api(libs.activity.compose)
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.tooling.preview)
    api(libs.compose.material3)
    api(libs.lifecycle.compose)
    api(libs.constraintlayout.compose)
    api(libs.icons.extended)

    api(libs.datastore)
    api(libs.datastore.preferences)

    api(libs.glideCompose)
    api(libs.glide)
    ksp(libs.glide.ksp)
    api(libs.glide.okhttp3.integration)

    api(libs.okhttp3)
    api(libs.okhttp3.logging.interceptor)

    api(libs.gson)

    api(libs.retrofit)
    api(libs.retrofit.converter.gson)

    api(libs.coroutines.core)
    api(libs.coroutines.android)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.runtime)

    api(libs.navigation.compose)
}