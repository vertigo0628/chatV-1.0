plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    // kotlin("kapt")  // Removed - not needed without Glide/Room
    id("kotlin-parcelize")
}

android {
    namespace = "com.university.chatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.university.chatapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
        viewBinding = true
        dataBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Firebase BOM - Updated to latest stable version
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    // implementation("com.google.firebase:firebase-storage-ktx")  // Disabled - requires billing
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // Image Loading - SIMPLIFIED (removed Glide to avoid kapt issues)
    // implementation("com.github.bumptech.glide:glide:4.16.0")
    // kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("io.coil-kt:coil:2.5.0")  // Modern alternative, no kapt needed
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Image Picker - REMOVED (causing build issues)
    // implementation("com.github.dhaval2404:imagepicker:2.1")

    // Camera - SIMPLIFIED
    // implementation("androidx.camera:camera-camera2:1.3.0")
    // implementation("androidx.camera:camera-lifecycle:1.3.0")
    // implementation("androidx.camera:camera-view:1.3.0")

    // Permissions - SIMPLIFIED
    // implementation("com.guolindev.permissionx:permissionx:1.7.1")

    // Media Player - REMOVED (not essential for basic chat)
    // implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Encryption - REMOVED (can add later)
    // implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Paging - REMOVED (not needed for basic version)
    // implementation("androidx.paging:paging-runtime-ktx:3.2.1")

    // Work Manager - REMOVED (not essential now)
    // implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Swipe Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Room Database - REMOVED (can use Firestore only)
    // implementation("androidx.room:room-runtime:2.6.1")
    // implementation("androidx.room:room-ktx:2.6.1")
    // kapt("androidx.room:room-compiler:2.6.1")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp - REMOVED (Firebase handles networking)
    // implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Force consistent Firebase and Google Play Services versions
configurations.all {
    resolutionStrategy {
        force("com.google.firebase:firebase-common:21.0.0")
        force("com.google.firebase:firebase-common-ktx:21.0.0")
        force("com.google.android.gms:play-services-basement:18.4.0")
        force("com.google.android.gms:play-services-base:18.5.0")
        force("com.google.android.gms:play-services-tasks:18.2.0")
    }
}