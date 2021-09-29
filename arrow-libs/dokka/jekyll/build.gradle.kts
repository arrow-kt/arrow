plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
}

dependencies {
  compileOnly(libs.dokka.core)
  implementation(libs.dokka.base)
  implementation(libs.dokka.gfmPluginx)
}
