rootProject.name = "arrow-optics-gradle-test-jvm"

pluginManagement {
  repositories {
    maven(url = file("../../build/local-plugin-repository"))
    mavenCentral()
  }
}
