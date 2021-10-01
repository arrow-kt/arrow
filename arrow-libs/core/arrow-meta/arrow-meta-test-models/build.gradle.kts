import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("org.jetbrains.kotlin.kapt")
}

dependencies {
  implementation(libs.kotlin.stdlibJDK8)
  implementation(libs.google.autoService)
  kapt(libs.google.autoService)
  implementation(projects.arrowAnnotations)
  implementation(projects.arrowCore)
}
