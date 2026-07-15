plugins {
  kotlin("multiplatform") version "2.4.10"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

kotlin {
  jvm()
  js().browser()

  applyDefaultHierarchyTemplate()
}
