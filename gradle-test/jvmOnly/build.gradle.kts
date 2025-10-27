import arrow.optics.plugin.arrowOptics

plugins {
  kotlin("jvm") version "2.2.21"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

kotlin {
  arrowOptics()
}
