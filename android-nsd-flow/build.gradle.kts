plugins {

    id("com.android.library")
    kotlin("android")
}


android {

    namespace = "com.github.aroio"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        // Enabling multidex support.
        multiDexEnabled = true
    }


    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("io.mockk:mockk:1.10.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.annotation:annotation:1.6.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")

    testImplementation("io.mockk:mockk:1.10.5")

    androidTestImplementation("io.mockk:mockk:1.10.5")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}
