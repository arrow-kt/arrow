plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("DOC_CREATION"))
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
