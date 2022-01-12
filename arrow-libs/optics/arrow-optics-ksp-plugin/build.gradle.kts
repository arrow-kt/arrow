plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

kotlin {
  explicitApi = null
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    jvmMain {
      dependencies {
        implementation(libs.ksp)
      }
    }
    jvmTest {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
        implementation(libs.junitJupiter)
        implementation(libs.junitJupiterEngine)
        implementation(libs.assertj)
        implementation(libs.classgraph)
        implementation(libs.kotlinCompileTesting)
        implementation(libs.kotlinCompileTestingKsp)
        runtimeOnly(projects.arrowOpticsKspPlugin)
        runtimeOnly(projects.arrowAnnotations)
        runtimeOnly(projects.arrowCore)
        runtimeOnly(projects.arrowOptics)
      }
    }
  }
}
