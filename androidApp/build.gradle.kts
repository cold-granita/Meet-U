plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.meetu_application.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.meetu_application.android"
        minSdk = 24
        targetSdk = 35
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
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose.android)
    debugImplementation(libs.compose.ui.tooling)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    implementation("androidx.compose.animation:animation:1.7.0-beta01")
    implementation("androidx.compose.animation:animation-graphics:1.7.0-beta01")

}