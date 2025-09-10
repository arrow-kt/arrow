import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
  alias(libs.plugins.kotlin.jsPlainObjects)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
  compilerOptions.freeCompilerArgs.addAll(
    "-Xexpect-actual-classes",
    "-Xcontext-parameters",
    "-Xallow-contracts-on-more-functions"
  )

  sourceSets {
    val nonJvmAndJsMain by creating { dependsOn(nonJvmMain.get()) }
    val nonJvmAndJsTest by creating { dependsOn(nonJvmTest.get()) }

    nativeMain.get().dependsOn(nonJvmAndJsMain)
    nativeTest.get().dependsOn(nonJvmAndJsTest)

    wasmJsMain.get().dependsOn(nonJvmAndJsMain)
    wasmJsTest.get().dependsOn(nonJvmAndJsTest)
    commonMain {
      dependencies {
        api(projects.arrowAtomic)
        api(projects.arrowAnnotations)
      }
    }

    jsMain {
      dependencies {
        implementation(libs.kotlinWrappers.js)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
