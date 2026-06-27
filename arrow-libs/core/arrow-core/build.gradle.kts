import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  id("arrow.kotlin")
  alias(libs.plugins.kotlin.jsPlainObjects)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
  sourceSets {
    val nonJvmAndJsMain = create("nonJvmAndJsMain") { dependsOn(nonJvmMain.get()) }
    val nonJvmAndJsTest = create("nonJvmAndJsTest") { dependsOn(nonJvmTest.get()) }

    nativeMain.get().dependsOn(nonJvmAndJsMain)
    nativeTest.get().dependsOn(nonJvmAndJsTest)

    wasmMain.get().dependsOn(nonJvmAndJsMain)
    wasmTest.get().dependsOn(nonJvmAndJsTest)
    
    commonMain {
      dependencies {
        api(projects.arrowAtomic)
        api(projects.arrowAnnotations)
        api(projects.arrowExceptionUtils)
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
