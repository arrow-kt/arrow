import org.jetbrains.kotlin.gradle.get

plugins {
  id("java-gradle-plugin")
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

sourceSets {
  main {
    java.srcDirs("src/main/kotlin")
  }
  test {
    java.srcDirs("src/test/kotlin")
  }
}

tasks {
  withType<ProcessResources> {
    filesMatching("**/plugin.properties") {
      filter { it.replace("%CURRENT_VERSION%", properties["projects.version"].toString()) }
      filter { it.replace("%KOTLIN_VERSION%", libs.versions.kotlin.get()) }
    }
  }
}

gradlePlugin {
  plugins {
    create("ank") {
      id = "ank-gradle-plugin"
      implementationClass = "arrow.ank.AnkPlugin"
    }
  }
}

tasks.whenTaskAdded {

}

tasks.whenTaskAdded {
  if (name.matches(".*Plugin.*MavenPublication.*".toRegex())) {
    enabled = false
  }
}
