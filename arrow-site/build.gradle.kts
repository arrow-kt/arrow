import arrow.ank.AnkExtension

buildscript {
    dependencies {
        classpath(libs.arrow.ankGradle)
    }
}

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

dependencies {
    implementation(projects.arrowCore)
    implementation(projects.arrowFxCoroutines)
    implementation(projects.arrowFxStm)
    implementation(projects.arrowOptics)
    compileOnly(libs.kotlin.reflect)

    kapt(projects.arrowMeta)
}

// Ank Plugin is not applied for every library to avoid adding runtime dependencies
apply(plugin = libs.plugins.arrowAnk.get().pluginId)

configure<AnkExtension> {
    source = file("/docs")
    target = file("$buildDir/site")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks {
    register<Exec>("generateAndValidateDoc") {
        workingDir = file("../arrow-libs")
        commandLine("sh", "gradlew", "buildDoc")
    }

    register<Exec>("generateSite") {
        commandLine("sh", "generate-site.sh")
    }
    
    register("buildSite") {
        group = "documentation"
        description = "Generates and validates the documentation, and generates the website"
        dependsOn("generateAndValidateDoc")
        dependsOn("generateSite")
    }

    named("generateSite").get().mustRunAfter("generateAndValidateDoc")

    named<Delete>("clean") {
        delete("$projectDir/docs/apidocs")
    }
}
