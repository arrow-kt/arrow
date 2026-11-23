rootProject.name = "arrow-optics-gradle-test-multiplatform"

pluginManagement {
  repositories {
    maven(url = file("../../build/local-plugin-repository"))
    mavenCentral()
  }
}
