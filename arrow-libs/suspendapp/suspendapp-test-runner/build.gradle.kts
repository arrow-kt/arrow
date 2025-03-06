import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id(libs.plugins.spotless.get().pluginId)
}

spotless {
  kotlin {
    ktlint().editorConfigOverride(mapOf("ktlint_standard_filename" to "disabled"))
  }
}

dependencies {
  implementation(kotlin("test"))
  testImplementation(libs.bundles.testing)
}

val testAppTasks = project(projects.suspendappTestApp.path).tasks
val jvmTask = testAppTasks.named<Jar>("shadowJar").orNull?.takeIf { it.enabled }
val nonJvmTasks = testAppTasks.withType<AbstractExecTask<*>>()
  .matching { it.enabled && (it.name.startsWith("runReleaseExecutable") || it.name.endsWith("NodeRun")) }

fun Jar.addToTestTask(test: Test) {
  test.dependsOn(this)
  test.systemProperty("jvmJar", this.outputs.files.singleFile.absolutePath)
}

fun AbstractExecTask<*>.addToTestTask(test: Test) {
  test.dependsOn(taskDependencies)
  test.systemProperty("$name.executable", executable!!)
  test.systemProperty("$name.workdir", workingDir.absolutePath)
  if (this is NodeJsExec) {
    test.systemProperty("$name.entrypoint", inputFileProperty.map { it.asFile.absolutePath }.get())
  }
}

// include all enabled test tasks in "test"
tasks.test {
  jvmTask?.addToTestTask(this)
  nonJvmTasks.all { addToTestTask(this@test) }
  useJUnitPlatform()
}

// expose per-target test task for JVM target
if (jvmTask != null) {
  tasks.register<Test>("jvmTest") {
    group = "verification"
    jvmTask.addToTestTask(this)
    useJUnitPlatform()
  }
}

// expose per-target test tasks for non-JVM targets
nonJvmTasks.all {
  val target = name
    .removePrefix("runReleaseExecutable")
    .removeSuffix("NodeRun")
    .let { it.first().lowercase() + it.drop(1) }
  val testTask = "${target}Test"
  tasks.register<Test>(testTask) {
    group = "verification"
    addToTestTask(this)
    useJUnitPlatform()
  }
}
