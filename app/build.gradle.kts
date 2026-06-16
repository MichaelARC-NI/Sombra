plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.michael.sombra"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.michael.sombra"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "android"
            keyAlias = "michael_sombra"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
