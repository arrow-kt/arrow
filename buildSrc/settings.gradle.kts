@file:Suppress("LocalVariableName")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "arrow-convention"

val kotlinRepoUrl: String? = providers.gradleProperty("kotlin_repo_url").orNull
val kotlinVersion: String? = providers.gradleProperty("kotlin_version").orNull
val kspVersion: String? = providers.gradleProperty("ksp_version").orNull
val composeVersion: String? = providers.gradleProperty("compose_version").orNull

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
      if (!kotlinVersion.isNullOrBlank()) {
        println("Overriding Kotlin version with $kotlinVersion")
        version("kotlin", kotlinVersion)
      }
      if (!kspVersion.isNullOrBlank()) {
        println("Overriding KSP version with $kspVersion")
        version("kspVersion", kspVersion)
      }
      if (!composeVersion.isNullOrBlank()) {
        println("Overriding Compose version with $composeVersion")
        version("composePlugin", composeVersion)
      }
    }
  }
}
