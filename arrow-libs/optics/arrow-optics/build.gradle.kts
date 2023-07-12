@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

apply(from = property("ANIMALSNIFFER_MPP"))

val enableCompatibilityMetadataVariant =
  providers.gradleProperty("kotlin.mpp.enableCompatibilityMetadataVariant")
    .orNull?.toBoolean() == true

if (enableCompatibilityMetadataVariant) {
  tasks.withType<Test>().configureEach {
    exclude("**/*")
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(libs.kotlin.stdlibCommon)
      }
    }
    if (!enableCompatibilityMetadataVariant) {
      commonTest {
        dependencies {
          implementation(libs.kotest.frameworkEngine)
          implementation(libs.kotest.assertionsCore)
          implementation(libs.kotest.property)
        }
      }
      jvmTest {
        dependencies {
          implementation(libs.kotlin.stdlib)
          implementation(libs.junitJupiterEngine)
          implementation(libs.kotlin.reflect)
        }
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
        api(libs.kotlin.reflect)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

//fun DependencyHandlerScope.kspTest(dependencyNotation: Any): Unit {
//  val exclude = setOf("commonTest", "nativeTest")
//  add("kspMetadata", dependencyNotation)
//  kotlin.sourceSets
//    .filter { it.name !in exclude && it.name.contains("Test") }
//    .forEach {
//      val task = "ksp${it.name.capitalize()}"
//      configurations.findByName(task)?.let {
//        add(task, dependencyNotation)
//      }
//    }
//}

//dependencies {
//  kspTest(projects.arrowOpticsKspPlugin)
//}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "arrow.optics"
  }
}
