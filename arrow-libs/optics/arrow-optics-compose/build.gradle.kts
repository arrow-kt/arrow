@file:Suppress("DSL_SCOPE_VIOLATION")

repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  // alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
  alias(libs.plugins.compose.jetbrains)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.android.library)
}

apply(from = property("ANIMALSNIFFER_MPP"))

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

kotlin {
  explicitApi()

  jvm()
  js(IR) {
    browser()
    nodejs()
  }
  androidTarget()
  // Native: https://kotlinlang.org/docs/native-target-support.html
  // -- Tier 1 --
  linuxX64()
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  // -- Tier 2 --
  // linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()
  iosArm64()
  // -- Tier 3 --
  mingwX64()

  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowOptics)
        api(libs.coroutines.core)
        api(compose.runtime)
        implementation(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

composeCompiler {
  // override the choice of Compose if we use a Kotlin -dev version
  val kotlinVersion = project.rootProject.properties["kotlin_version"] as? String
  if (kotlinVersion != null && kotlinVersion.contains("-dev-")) {
    ext["suppressKotlinVersionCompatibilityCheck"] = kotlinVersion
  }
}

android {
  namespace = "arrow.optics.compose"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
}

tasks.named<Jar>("jvmJar").configure {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.optics.compose"
  }
}

// https://youtrack.jetbrains.com/issue/KT-68095/MPP-Compose-Kover-Cannot-expand-ZIP-build-kover-default.artifact
val compileTargetsThatNeedKoverFix = listOf("iosSimulatorArm64", "iosX64", "iosArm64", "watchosSimulatorArm64", "watchosX64", "macosArm64", "macosX64", "tvosSimulatorArm64", "tvosX64", "js", "mingwX64", "linuxX64")

afterEvaluate {
  for (task in compileTargetsThatNeedKoverFix) {
    tasks.named("${task}ResolveResourcesFromDependencies") {
      doFirst {
        rootProject.subprojects.forEach {
          delete(it.layout.buildDirectory.file("kover/default.artifact"))
        }
      }
    }
  }
}
