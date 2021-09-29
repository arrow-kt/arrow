import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id(libs.plugins.arrowGradleConfig.jvm.get().pluginId)
  id("org.jetbrains.kotlin.kapt")
}

dependencies {
  implementation(libs.kotlin.stdlibJDK8)
  implementation(libs.google.autoService)
  kapt(libs.google.autoService)
  implementation(projects.arrowAnnotations)
  implementation(projects.arrowCore)
}
