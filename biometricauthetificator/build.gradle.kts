import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "io.github.zaval"
version = "1.1.0"

kotlin {
//    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.runtime)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(compose.foundation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.security.crypto)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)

            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit) // Or a more recent version
                implementation(libs.mockk) // Or a more recent version
                implementation(libs.robolectric)
                implementation(libs.androidx.core.ktx)
            }
        }

    }
}

android {
    namespace = "io.github.zaval.biometricauth"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

//dependencies {
//    implementation(libs.androidx.core.ktx)
//}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "biometricauth", version.toString())

    pom {
        name = "Biometric authentificator"
        description = "Authentification library uses fingerprint or FaceID."
        inceptionYear = "2025"
        url = "https://github.com/zaval/biometric-auth-ktx/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "zaval"
                name = "Dmytrii Zavalnyi"
                url = "https://github.com/zaval"
            }
        }
        scm {
            url = "https://github.com/zaval/biometric-auth-ktx"
            connection = "scm:git:git://github.com/zaval/biometric-auth-ktx.git"
            developerConnection = "scm:git:ssh://git@github.com/zaval/biometric-auth-ktx.git"
        }
    }
}
