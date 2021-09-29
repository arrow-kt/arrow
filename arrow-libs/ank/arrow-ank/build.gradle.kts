plugins {
    alias(libs.plugins.arrowGradleConfig.jvm)
    alias(libs.plugins.arrowGradleConfig.publishJvm)
}

apply(from = property("DOC_CREATION"))

dependencies {
    implementation(projects.arrowFxCoroutines)
    implementation(libs.kotlin.stdlibJDK8)

    implementation(libs.kotlin.compiler)
    implementation(libs.kotlin.scriptUtil)
    runtimeOnly(libs.kotlin.reflect)
    runtimeOnly(libs.kotlin.scriptingCompiler)
}
