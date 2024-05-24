plugins {
    alias(libs.plugins.andr.app)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.daggerHilt.android)
    alias(libs.plugins.kotlin.dokka)
}

android {

    namespace = "world.inetumrealdolmen.mobiletrm"
    compileSdk = 34

    defaultConfig {
        applicationId = "world.inetumrealdolmen.mobiletrm"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "world.inetumrealdolmen.mobiletrm.util.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["auth0Domain"] = "@string/auth0_domain"
        manifestPlaceholders["auth0Scheme"] = "@string/auth0_scheme"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel8api32") {
                    device = "Pixel 8"
                    apiLevel = 32
                    systemImageSource = "aosp"
                }
            }
        }
        animationsDisabled = false
    }

    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Dependency Injection with Hilt using KSP
    implementation(libs.hilt.android)
    implementation(libs.androidx.appcompat)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    // Android Core
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.navigation.runtime.ktx)

    // Android Notifications
    implementation(libs.google.accompanist)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.coil)

    // Authentication
    implementation(libs.auth0.android)

    // API
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.scalars)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.work.runtime.ktx)

    // Beacon
    implementation(libs.android.beacon.library)

    // Kotlin extended libraries
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlin.reflect)

    // RoomDB
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.livedata)

    // Debugging
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.navigation.testing)
    kspAndroidTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.google.hilt.compiler)
    androidTestImplementation(libs.androidx.work.test)
}

hilt {
    enableAggregatingTask = false
}

dependencyCheck {
//    failBuildOnCVSS = 7F // fail build when OWASP vulnerabilities are labeled High risk or higher
    nvd.apiKey = System.getenv("NVD_API_KEY")
}
