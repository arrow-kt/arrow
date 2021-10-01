plugins {
    alias(libs.plugins.arrowGradleConfig.multiplatform)
    alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

apply(plugin = "io.kotest.multiplatform")
apply(from = property("TEST_COVERAGE"))
apply(from = property("DOC_CREATION"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.arrowCore)
                api(libs.coroutines.core)
                implementation(libs.kotlin.stdlibCommon)
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
                implementation(libs.coroutines.test)
            }
        }
        jsMain {
            dependencies {
                implementation(libs.kotlin.stdlibJS)
            }
        }
    }
}
