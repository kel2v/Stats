import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use {
        keystoreProperties.load(it)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.room)
}

android {
    namespace = "com.bytemanager.stats"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.bytemanager.stats"
        minSdk = 29
        targetSdk = 36

        versionCode = 4
        versionName = "1.2.0"
    }

    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.toString()?.let {rootProject.file(it)}
            storePassword = keystoreProperties["storePassword"]?.toString()
            keyPassword = keystoreProperties["keyPassword"]?.toString()
            keyAlias = keystoreProperties["keyAlias"]?.toString()
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }

        unitTests.all {
            it.jvmArgs(
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "--add-opens=java.base/java.util=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-opens=java.base/java.net=ALL-UNNAMED",
                "--add-opens=java.base/java.security=ALL-UNNAMED",
                "--add-opens=java.base/java.text=ALL-UNNAMED",
                "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
                "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
                "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
            )

            it.useJUnitPlatform()
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // androidx.compose.bom
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // other androidx dependencies
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    /*       */ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    /*       */ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)


    // google.dagger.hilt
    implementation(libs.google.dagger.hilt.android)
    /*       */ksp(libs.google.dagger.hilt.android.compiler)
    /*       */ksp(libs.google.dagger.hilt.compiler)

    // patrykandpatrick.vico
    implementation(libs.patrykandpatrick.vico.compose)








    // =================== test ====================

    // androidx
    testImplementation(libs.androidx.test.core.ktx)

    // test: Junit
    testImplementation(libs.junit)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit.jupiter)
    /*
    **/testRuntimeOnly(libs.junit.platform.launcher)

    // test: jetbrains
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)

    // test: robolectric
    testImplementation(libs.robolectric)

    // test: kotlin
    testImplementation(kotlin("test"))






    // ================ androidTest =====================

    // androidx
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.junit.ktx)
    androidTestImplementation(libs.androidx.test.runner)

    androidTestImplementation(libs.hilt.android.testing)




    // ============ Debug ====================

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}