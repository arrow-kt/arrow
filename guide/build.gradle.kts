import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)
    implementation(projects.arrowFxCoroutines)
    implementation(projects.arrowFxStm)
    implementation(projects.arrowOptics)
    implementation(libs.kotest.assertionsCore)
    implementation(libs.kotest.property)

    testImplementation(projects.arrowCoreTest)
    testImplementation(libs.kotest.runnerJUnit5)
    testImplementation(libs.kotest.frameworkEngine)
}

sourceSets.test {
    java.srcDirs("example", "test")
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            setExceptionFormat("full")
            setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}
