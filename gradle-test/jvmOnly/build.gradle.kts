plugins {
  kotlin("jvm") version "2.4.0"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

dependencies {
  implementation("io.arrow-kt:arrow-optics:10.0-test")
}
