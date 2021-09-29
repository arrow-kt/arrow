plugins {
    alias(libs.plugins.arrowGradleConfig.multiplatform)
    alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))
apply(from = property("DOC_CREATION"))
apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.arrowCore)
                compileOnly(libs.kotlin.stdlibCommon)
                implementation(libs.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.arrowFxCoroutinesTest)
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
