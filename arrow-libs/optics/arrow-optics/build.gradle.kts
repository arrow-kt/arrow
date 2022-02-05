plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
//  alias(libs.plugins.ksp)
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
