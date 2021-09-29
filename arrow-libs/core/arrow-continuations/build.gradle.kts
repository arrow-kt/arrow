plugins {
    alias(libs.plugins.arrowGradleConfig.multiplatform)
    alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

apply(from = property("TEST_COVERAGE"))
apply(from = property("DOC_CREATION"))
apply(from = property("ANIMALSNIFFER_MPP"))
apply(plugin = "io.kotest.multiplatform")

kotlin {
    sourceSets {
        commonMain {
            dependencies {
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
