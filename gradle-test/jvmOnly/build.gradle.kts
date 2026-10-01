plugins {
  kotlin("jvm") version "2.5.0-RC"
  kotlin("plugin.serialization") version "2.5.0-RC"
  id("io.arrow-kt.optics") version "10.0-test"
}

repositories {
  maven(url = file("../../build/local-plugin-repository"))
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.13.0")
}
