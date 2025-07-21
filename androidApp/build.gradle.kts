plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.meetu_application.android"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.meetu_application.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.material3.v120)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.gson)
    implementation (libs.accompanist.navigation.animation)
    implementation(libs.androidx.animation.v170beta01)
    implementation(libs.androidx.animation.graphics)

    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.navigation.compose)

    implementation (libs.core)
    implementation (libs.androidx.ui.graphics)
    

    implementation(libs.androidx.security.crypto.ktx.v110alpha03)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
}