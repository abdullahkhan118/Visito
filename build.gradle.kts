// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
//        maven {
//            url = "https://maven.tomtom.com:8443/nexus/content/repositories/releases/"
//        }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.2")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("com.google.firebase:perf-plugin:1.4.2")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//    }
//}
