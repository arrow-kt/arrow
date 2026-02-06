plugins {
  id("arrow.kotlin")
  alias(libs.plugins.compose.jetbrains)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowOptics)
        api(libs.coroutines.core)
        api("org.jetbrains.compose.runtime:runtime:1.10.0")
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.bundles.testing)
      }
    }
  }
}

composeCompiler {
  // override the choice of Compose if we use a Kotlin -dev version
  val kotlinVersion = project.rootProject.properties["kotlin_version"] as? String
  if (kotlinVersion != null && kotlinVersion.contains("-dev-")) {
    ext["suppressKotlinVersionCompatibilityCheck"] = kotlinVersion
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
