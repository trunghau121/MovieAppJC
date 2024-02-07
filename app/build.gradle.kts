import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.movieappjc"
        minSdk = 24
        targetSdk = 34
        versionCode = getVersionCode()
        versionName = appVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    signingConfigs {
        create("product") {
            keyAlias = "test"
            keyPassword = "test@#123"
            storeFile = project.rootProject.file("keystore/test_keys.jks")
            storePassword = "test@#123"
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
            val outputDir = project.buildDir.path + "/compose-reports"
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
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
}