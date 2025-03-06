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
val jvmTask = testAppTasks.named<Jar>("shadowJar")
val nonJvmTasks = testAppTasks.withType<AbstractExecTask<*>>()
  .matching { it.enabled && (it.name.startsWith("runReleaseExecutable") || it.name.endsWith("NodeRun")) }

tasks.register<Task>("prepareExecutables") {
  dependsOn(jvmTask)
  nonJvmTasks.all { this@register.dependsOn(taskDependencies) }
}

tasks.test {
  dependsOn("prepareExecutables")
  systemProperty("jvmJar", jvmTask.map { it.outputs.files.singleFile.absolutePath }.get())
  nonJvmTasks.all {
    val name = name
    systemProperties(
      "${name}.executable" to executable,
      "${name}.workdir" to workingDir.absolutePath,
    )
    if (this is NodeJsExec) {
      systemProperties("${name}.entrypoint" to inputFileProperty.map { it.asFile.absolutePath }.get())
    }
  }
  useJUnitPlatform()
}
