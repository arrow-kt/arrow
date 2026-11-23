plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  pluginDevKit("compiler-plugin")
  id("arrow.spotless")
  id("arrow.publish")
}

group = property("projects.group").toString()

pluginDevKit {
  // testDataLibraries { /* `register`ing conditional libraries */ }
  componentRegistrar = "arrow.optics.plugin.fir.OpticsPluginComponentRegistrar"
  commandLineProcessor = "arrow.optics.plugin.fir.OpticsCommandLineProcessor"
  generateTestsEnabled = false

  pluginPackage = "arrow.optics.plugin"

  cliVersions(min = "2.3.0", max = "2.5.0", org.jetbrains.kotlin.compiler.plugin.devkit.BetaAndRc.LATEST)
  ideaVersions(min = "261", 263, includeRc = true, includeEap = true)

  versionHierarchy {
    splitDev(2, 3, 20)
  }
}

kotlin {
  sourceSets {
    commonMain {
      kotlin.setSrcDirs(listOf("src/main/kotlin"))
    }
    commonTest {
      kotlin.setSrcDirs(listOf("src/test/kotlin"))

      dependencies {
        implementation(kotlin("test"))
        implementation(libs.kotest.assertionsCore)
        implementation(libs.classgraph)
        implementation(kotlin("serialization-compiler-plugin-embeddable"))
        implementation(libs.kotlinCompileTesting.get().toString()) {
          exclude(
            group = libs.classgraph.get().module.group,
            module = libs.classgraph.get().module.name
          )
          exclude(
            group = "org.jetbrains.kotlin",
            module = "kotlin-stdlib"
          )
        }
        runtimeOnly(projects.arrowAnnotations)
        runtimeOnly(projects.arrowOptics)
        runtimeOnly(projects.arrowCore)
      }
    }
  }

  compilerOptions { commonCompilerOptions(providers) }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  systemProperty("arrowVersion", project(":arrow-optics").version.toString())
}
