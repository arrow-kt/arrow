import arrow.optics.plugin.arrowOptics

plugins {
  kotlin("jvm") version "2.3.10"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

kotlin {
  arrowOptics()
}
