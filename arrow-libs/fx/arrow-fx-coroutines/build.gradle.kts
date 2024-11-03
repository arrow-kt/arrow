@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.time.Duration


plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.publish)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowCore)
        api(projects.arrowAutoclose)
        api(libs.coroutines.core)
        implementation(libs.kotlin.stdlib)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.arrowCore)
        implementation(projects.arrowAtomic)
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
        attributes["Automatic-Module-Name"] = "arrow.fx.coroutines"
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

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
