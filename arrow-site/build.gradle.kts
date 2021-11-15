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
