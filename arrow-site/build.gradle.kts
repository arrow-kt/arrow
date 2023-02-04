@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.arrowCore)
    implementation(projects.arrowFxCoroutines)
    implementation(projects.arrowFxStm)
    implementation(projects.arrowOptics)
    ksp(projects.arrowOpticsKspPlugin)
    compileOnly(libs.kotlin.reflect)
}

tasks {
    register<Exec>("generateSite") {
        commandLine("sh", "generate-site.sh")
    }
    
    register("buildSite") {
        group = "documentation"
        description = "Generates and validates the documentation, and generates the website"
        dependsOn("buildDoc")
        dependsOn("generateSite")
    }

    named("generateSite").get().mustRunAfter("buildDoc")

    named<Delete>("clean") {
        delete("$projectDir/docs/apidocs")
    }
}
