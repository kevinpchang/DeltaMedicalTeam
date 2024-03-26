import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude


plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
    alias(libs.plugins.jetbrainsKotlinAndroid)


}



android {
    namespace = "com.csc131.deltamedicalteam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.csc131.deltamedicalteam"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }


}

dependencies {
    implementation("com.balysv:material-ripple:1.0.2")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
//    implementation("com.mikhaellopez:circularimageview:3.2.0")



    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.preference)
    implementation(libs.annotation)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}