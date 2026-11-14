plugins {
  kotlin("multiplatform") version "2.4.21"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
  maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
}

kotlin {
  jvm()
  js().browser()

  applyDefaultHierarchyTemplate()
}
