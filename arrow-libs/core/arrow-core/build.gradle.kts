import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
  compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
  compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")

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

    commonTest {
      dependencies {
        implementation(projects.arrowFxCoroutines)
        implementation(projects.arrowPlatform)
        implementation(libs.bundles.testing)
      }
    }
  }
}
