rootProject.name = "arrow-optics-gradle-test-jvm"

pluginManagement {
  repositories {
    maven(url = file("../../build/local-plugin-repository"))
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/compiler-plugin-dev-kit/eap")
  }
}
