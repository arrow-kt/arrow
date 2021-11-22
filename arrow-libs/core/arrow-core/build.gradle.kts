plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(projects.arrowContinuations)
        api(projects.arrowAnnotations)
        implementation(libs.kotlin.stdlibCommon)
      }
    }
    commonTest {
      dependencies {
        implementation(projects.arrowCoreTest)
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }
    jvmTest {
      dependencies {
        runtimeOnly(libs.kotest.runnerJUnit5)
      }
    }
    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

setupDokka()

dependencies {
  dokkaGfmPlugin("io.arrow-kt:arrow-gradle-config-dokka")
}

fun Project.setupDokka(baseUrl: String = properties["pom.smc.url"]?.toString() ?: "") {
  afterEvaluate {
    tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaGfm") {
      outputDirectory.set(file("${rootProject.rootDir}/arrow-site/docs"))
      extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
        dokkaSourceSets.named(kotlinSourceSet.name) {
          perPackageOption {
            matchingRegex.set(".*\\.internal.*") // match all .internal packages and sub-packages
            suppress.set(true)
          }
          skipDeprecated.set(true)
          reportUndocumented.set(false)
          kotlinSourceSet.kotlin.srcDirs.forEach { srcDir ->
            sourceLink {
              localDirectory.set(srcDir)
              remoteUrl.set(uri("$baseUrl/${srcDir.relativeTo(rootProject.rootDir)}").toURL())
              remoteLineSuffix.set("#L")
            }
          }
        }
      }
    }
  }
}
