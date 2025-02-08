import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

val keystoreProperties = project.rootProject.file("key.properties")
val properties = Properties()
properties.load(keystoreProperties.inputStream())

val appVersion = project.rootProject.file("version.txt").readText().trim()
fun getVersionCode(): Int {
    val (versionMajor, versionMinor, versionPatch) = appVersion.split(".")
    return versionMajor.toInt() * 100 + versionMinor.toInt() * 10 + versionPatch.toInt()
}

android {
    namespace = "com.movieappjc"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.movieappjc"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = getVersionCode()
        versionName = appVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    signingConfigs {
        create("product") {
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
            storeFile = project.rootProject.file(properties.getProperty("storeFile"))
            storePassword = properties.getProperty("storePassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("product")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        if (project.findProperty("enableComposeCompilerReports") == "true") {
            val outputDir = project.layout.buildDirectory.set(File("/compose-reports"))
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$outputDir",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$outputDir"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    implementation(project(":core-app"))
    implementation(libs.readmore.material)
    implementation(libs.youtubeplayer)
    // Room
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.tooling.preview)
    debugImplementation (libs.compose.ui.tooling)

    //photo picker
    implementation(libs.dhaval2404.imagepicker)

    //sdp
    implementation(libs.intuit.sdp.android)
    implementation(libs.legacy.support.v4)

    //camera x
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")
    implementation("androidx.camera:camera-extensions:1.4.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
    implementation("com.google.mlkit:digital-ink-recognition:18.1.0")
    implementation("cz.adaptech.tesseract4android:tesseract4android:4.8.0")
    implementation("com.quickbirdstudios:opencv:4.5.3.0")
}