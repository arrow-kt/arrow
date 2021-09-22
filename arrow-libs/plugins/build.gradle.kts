plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(gradleApi())
  implementation(kotlin("stdlib"))
}

gradlePlugin {
  plugins {
    register("mpp-publish") {
      id = "mpp-publish"
      implementationClass = "arrow.gradle.MppPublishingPlugin"
    }
  }
}
