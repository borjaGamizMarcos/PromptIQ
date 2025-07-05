plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("org.jetbrains.kotlin.kapt")
}
configurations.all {
    exclude(group = "org.slf4j", module = "jcl-over-slf4j")
}

android {
    namespace = "com.example.promptiq"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.promptiq"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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

    // Para ViewModel en Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation("androidx.compose.ui:ui-text:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    kapt(libs.androidx.room.compiler)
    implementation("androidx.compose.foundation:foundation-layout:1.5.4")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")

    // CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")


    // ML Kit Face Detection
    implementation("com.google.mlkit:face-detection:16.1.6")
    implementation("com.google.accompanist:accompanist-permissions:0.31.5-beta")


    // Para PDFs básicos
    implementation("com.tom-roush:pdfbox-android:1.8.10.3"){
        exclude(group="commons-logging", module="commons-logging")
    }

    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml-lite:5.2.3")
    // Apache POI para leer .docx (solo la parte necesaria para Android)
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("commons-logging:commons-logging:1.2")





}