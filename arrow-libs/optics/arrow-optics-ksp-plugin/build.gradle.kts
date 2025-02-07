plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

kotlin {
  explicitApi = null
}

dependencies {
  implementation(libs.ksp)

  testImplementation(kotlin("test"))
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.classgraph)
  testImplementation(libs.kotlinCompileTesting) {
    exclude(
      group = libs.classgraph.get().module.group,
      module = libs.classgraph.get().module.name
    )
    exclude(
      group = "org.jetbrains.kotlin",
      module = "kotlin-stdlib"
    )
  }
  testImplementation(libs.kotlinCompileTestingKsp)
  testRuntimeOnly(projects.arrowOpticsKspPlugin)
  testRuntimeOnly(projects.arrowAnnotations)
  testRuntimeOnly(projects.arrowCore)
  testRuntimeOnly(projects.arrowOptics)
}

tasks.withType<Test>().configureEach {
  maxParallelForks = 1
  useJUnitPlatform()
}
