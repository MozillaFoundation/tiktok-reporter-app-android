@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.python)
    alias(libs.plugins.glean)
}

android {
    namespace = "org.mozilla.tiktokreporter"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.mozilla.tiktokreporter"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        release {
            isDebuggable = false
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            buildConfigField("String", "BASE_URL", "\"https://tiktok-reporter-app-be-jbrlktowcq-ew.a.run.app/\"")
            buildConfigField("String", "STORAGE_URL", "\"https://storage.googleapis.com/regrets_reporter_recording_docs/\"")
            buildConfigField("String", "UPLOAD_RECORDING_API_KEY", "\"f0bfa33e-333b-4704-b57e-bbe6e766ba65\"")
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false

            buildConfigField("String", "BASE_URL", "\"https://tiktok-reporter-app-be-jbrlktowcq-ew.a.run.app/\"")
            buildConfigField("String", "STORAGE_URL", "\"https://storage.googleapis.com/regrets_reporter_recording_docs/\"")
            buildConfigField("String", "UPLOAD_RECORDING_API_KEY", "\"f0bfa33e-333b-4704-b57e-bbe6e766ba65\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvm.target.get()
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.material3)
    implementation(libs.compose.constraintlayout)

    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.permissions)

    implementation(libs.media3.common)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.transformer)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.documentfile)
    implementation(libs.glean)
    implementation(libs.mozilla.components.service.glean)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.compose.markdown)
}