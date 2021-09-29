plugins {
    alias(libs.plugins.arrowGradleConfig.multiplatform)
    alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
    id("org.jetbrains.kotlin.kapt")
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
                implementation(libs.kotlin.stdlibCommon)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.arrowOpticsTest)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.kotlin.stdlibJDK8)
            }
        }
        jvmTest {
            kotlin.srcDirs("/build/generated/source/kapt/test")

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

dependencies {
    "kaptTest"(projects.arrowMeta)
}
