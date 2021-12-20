plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.ksp)
}

apply(plugin = "io.kotest.multiplatform")

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlibCommon)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowOpticsTest)
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }
    jvmTest {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
        implementation(libs.junitJupiterEngine)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

dependencies {
  kspJvmTest(projects.arrowOpticsKsp)
  kspJsTest(projects.arrowOpticsKsp)
  kspLinuxX64Test(projects.arrowOpticsKsp)
  kspMingwX64Test(projects.arrowOpticsKsp)
  kspIosX64Test(projects.arrowOpticsKsp)
  // kspAndroidNativeX64Test(projects.arrowOpticsKsp)
  // kspAndroidNativeArm64Test(projects.arrowOpticsKsp)
}
