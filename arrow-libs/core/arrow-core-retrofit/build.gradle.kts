plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("ANIMALSNIFFER"))

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  compileOnly(projects.arrowCore)
  compileOnly(libs.squareup.retrofit)
  testCompileOnly(libs.kotlin.reflect)
  testRuntimeOnly(libs.kotest.runnerJUnit5)
  testImplementation(projects.arrowCoreTest)
  testImplementation(libs.squareup.retrofitConverterGson)
  testImplementation(libs.squareup.okhttpMockWebServer)
}
