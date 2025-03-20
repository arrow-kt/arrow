import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.spotless.get().pluginId)
  alias(libs.plugins.shadow)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

kotlin {
  jvm()
  macosX64().binaries.executable()
  macosArm64().binaries.executable()
  linuxArm64().binaries.executable()
  linuxX64().binaries.executable()
  js(IR) {
    nodejs()
    binaries.executable()
  }
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    nodejs()
    binaries.executable()
  }

  applyDefaultHierarchyTemplate()
  sourceSets {
    commonMain {
      dependencies {
        implementation(kotlin("stdlib"))
        implementation(projects.arrowFxCoroutines)
        implementation(projects.suspendapp)
      }
    }
  }
}

tasks.named<ShadowJar>("shadowJar") {
  manifest {
    attributes("Main-Class" to "MainKt")
  }
}
