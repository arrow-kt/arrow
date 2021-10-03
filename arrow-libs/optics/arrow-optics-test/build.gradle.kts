plugins {
    alias(libs.plugins.arrowGradleConfig.multiplatform)
    alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

publishMultiplatform {
    isDokkaEnabled = false
}

apply(from = property("ANIMALSNIFFER_MPP"))

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.arrowOptics)
                api(projects.arrowCoreTest)
                api(libs.coroutines.core)
                api(libs.kotest.assertionsCore)
                api(libs.kotest.frameworkEngine)
                api(libs.kotest.property)
                implementation(libs.kotlin.stdlibCommon)
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
