import arrow.optics.plugin.arrowOpticsCommon

plugins {
  kotlin("multiplatform") version "2.2.21"
  id("arrow.optics.plugin") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

kotlin {
  jvm()
  js().browser()

  applyDefaultHierarchyTemplate()

  arrowOpticsCommon()
}
