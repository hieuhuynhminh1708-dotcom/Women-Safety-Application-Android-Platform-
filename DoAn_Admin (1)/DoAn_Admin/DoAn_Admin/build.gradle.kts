// file: build.gradle (Project Level)

plugins {
    // Cập nhật Android Gradle Plugin (AGP) để tránh cảnh báo Gradle 9.0
    id("com.android.application") version "8.5.1" apply false
    id("com.android.library") version "8.5.1" apply false

    // Cập nhật Plugin Kotlin lên 2.0.0
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // Phiên bản Google Services này vẫn ổn
    id("com.google.gms.google-services") version "4.4.4" apply false
}