@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.time.Duration

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.spotless)
  alias(libs.plugins.kotlinx.kover)
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowFxCoroutines)
        api(projects.arrowAtomic)
        api(libs.coroutines.core)
        implementation(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.coroutines.test)
        implementation(libs.kotest.assertionsCore)
        implementation(libs.kotest.property)
      }
    }
  }

  jvm {
    tasks.jvmJar {
      manifest {
        attributes["Automatic-Module-Name"] = "arrow.collectors"
      }
    }
  }

  js {
    nodejs {
      testTask {
        useMocha {
          timeout = "60s"
        }
      }
    }
    browser {
      testTask {
        useKarma {
          useChromeHeadless()
          timeout.set(Duration.ofMinutes(1))
        }
      }
    }
  }

  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  compilerOptions {
    (project.rootProject.properties["kotlin_language_version"] as? String)?.also { languageVersion = KotlinVersion.fromVersion(it) }
    (project.rootProject.properties["kotlin_api_version"] as? String)?.also { apiVersion = KotlinVersion.fromVersion(it) }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
