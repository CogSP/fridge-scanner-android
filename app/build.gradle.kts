plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.fridgescanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fridgescanner"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.barcode.scanning.v1702)
    implementation(libs.barcode.scanning.v1700)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation("androidx.compose.material:material-icons-extended:$1.4.3")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.play.services.code.scanner)
    implementation(libs.okhttp)

//    implementation(libs.androidx.camera.core.v110)
//    implementation(libs.androidx.camera.camera2.v110)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)
    //implementation(libs.accompanist.camera)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.camera.core.v130)
    implementation(libs.androidx.camera.camera2.v130)
    implementation(libs.androidx.camera.lifecycle.v130)
    implementation(libs.androidx.camera.view.v130)
}