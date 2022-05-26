import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    // Need add android as target since there are some method was marked experimental
    // in androidMain but not in commonMain(eg. AnimatedVisibility),
    android()
    jvm("desktop")
    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
            }
        }
        // TODO should specify target for sourceSet
        val jvmMain = create("jvmMain") {
            dependsOn(commonMain)
        }
        getByName("desktopMain") {
            dependsOn(jvmMain)
        }
        getByName("androidMain") {
            dependsOn(jvmMain)
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/resources", "src/commonMain/resources")
        }
    }
}
