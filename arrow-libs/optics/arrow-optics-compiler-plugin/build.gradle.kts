plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id("arrow.kotlin")
}

kotlin {
  explicitApi = null
  compilerOptions {
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
  }
}

dependencies {
  compileOnly(kotlin("compiler"))
}

