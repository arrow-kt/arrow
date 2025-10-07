import arrow.optics.plugin.arrowOptics

plugins {
  kotlin("jvm") version "2.2.20"
  id("arrow.optics.plugin") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

kotlin {
  arrowOptics()
}
