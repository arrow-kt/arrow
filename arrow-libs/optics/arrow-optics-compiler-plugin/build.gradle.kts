plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

kotlin {
  explicitApi = null
  compilerOptions {
    // optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    // optIn.add("org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi")
    // optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}

dependencies {
  compileOnly(kotlin("compiler"))

  testImplementation(kotlin("test"))
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.classgraph)
  testImplementation(kotlin("serialization-compiler-plugin-embeddable"))
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

// Compilers the tests also run on, each with its own test task in `check`. The default `test` task runs on the
// compiler kotlin-compile-testing depends on. The tests and plugin are compiled once; each compiler only changes
// the runtime classpath, so these runs check binary compatibility.
val testCompilers = listOf("2.5.0-Beta2")

testCompilers.forEach { version ->
  val suffix = "Kotlin" + version.replace(Regex("[^A-Za-z0-9]"), "_")
  val testRuntimeClasspath = configurations.testRuntimeClasspath.get()
  val runtimeClasspath = configurations.create("test${suffix}RuntimeClasspath") {
    isCanBeConsumed = false
    extendsFrom(testRuntimeClasspath)
    // replicate testRuntimeClasspath's attributes for the same variant resolution
    @Suppress("UnstableApiUsage")
    attributes.addAllLater(testRuntimeClasspath.attributes)
    resolutionStrategy.eachDependency {
      if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-")) useVersion(version)
    }
  }
  val testTask = tasks.register<Test>("test$suffix") {
    description = "Runs the tests on Kotlin $version."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().output + sourceSets.main.get().output + runtimeClasspath
    // Kotlin 2.5+ needs JDK 17 to run the compiler.
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(17)) })
  }
  tasks.check { dependsOn(testTask) }
}

// Kotlin 2.5 reflects over its own classes' fields and fails on the field coverage instrumentation adds.
kover {
  currentProject {
    instrumentation {
      excludedClasses.add("org.jetbrains.kotlin.*")
    }
  }
}

tasks.withType<Test>().configureEach {
  maxParallelForks = 1
  useJUnitPlatform()
}
