plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

kotlin {
  explicitApi = null
  compilerOptions {
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    optIn.add("org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi")
    optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}

dependencies {
  compileOnly(kotlin("compiler"))

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
  testRuntimeOnly(projects.arrowAnnotations)
  testRuntimeOnly(projects.arrowCore)
  testRuntimeOnly(projects.arrowOptics)
}

tasks.withType<Test>().configureEach {
  maxParallelForks = 1
  useJUnitPlatform()
}
