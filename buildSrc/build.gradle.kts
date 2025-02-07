repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(libs.gradlePlugin.kotlin.base)
  implementation(libs.gradlePlugin.kotlin.multiplatform)
  implementation(libs.gradlePlugin.android.library)
  implementation(libs.gradlePlugin.spotless)
  implementation(libs.gradlePlugin.animalSniffer)
  implementation(libs.gradlePlugin.dokka)
}
