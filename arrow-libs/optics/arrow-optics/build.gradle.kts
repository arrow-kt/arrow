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
  }
}

fun DependencyHandlerScope.kspAll(dependencyNotation: Any): Unit {
  val exclude = setOf("commonMain", "commonTest", "nativeMain", "nativeTest")
  add("kspMetadata", dependencyNotation)
  kotlin.sourceSets
    .filter { it.name !in exclude }
    .forEach {
      val task = "ksp${it.name.capitalize().removeSuffix("Main")}"
      add(task, dependencyNotation)
    }
}

kotlin.sourceSets.commonTest {
  kotlin.srcDir("build/generated/ksp/commonTest/kotlin")
}
//tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
//  if (name != "kspKotlinMetadata") {
//    dependsOn("kspKotlinMetadata")
//  }
//}
dependencies {
//  add("kspMetadata", libs.arrow.optics.ksp)
  kspAll(libs.arrow.optics.ksp)
}
