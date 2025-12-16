plugins {
    `version-catalog`
    id(libs.plugins.publish.get().pluginId)
}

mavenPublishing {
    configureBasedOnAppliedPlugins()
    pomFromGradleProperties()
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
}

catalog {
    versionCatalog {
        version("arrow", project.version.toString())

        library("annotations", "io.arrow-kt", "arrow-annotations").versionRef("arrow")
        library("atomic", "io.arrow-kt", "arrow-atomic").versionRef("arrow")
        library("autoclose", "io.arrow-kt", "arrow-autoclose").versionRef("arrow")
        library("exception-utils", "io.arrow-kt", "arrow-exception-utils").versionRef("arrow")
        library("collectors", "io.arrow-kt", "arrow-collectors").versionRef("arrow")
        library("core", "io.arrow-kt", "arrow-core").versionRef("arrow")
        library("core-high-arity", "io.arrow-kt", "arrow-core-high-arity").versionRef("arrow")
        library("core-jackson", "io.arrow-kt", "arrow-core-jackson").versionRef("arrow")
        library("core-jackson2", "io.arrow-kt", "arrow-core-jackson2").versionRef("arrow")
        library("core-retrofit", "io.arrow-kt", "arrow-core-retrofit").versionRef("arrow")
        library("core-serialization", "io.arrow-kt", "arrow-core-serialization").versionRef("arrow")
        library("core-result4k", "io.arrow-kt", "arrow-core-result4k").versionRef("arrow")
        library("eval", "io.arrow-kt", "arrow-eval").versionRef("arrow")
        library("functions", "io.arrow-kt", "arrow-functions").versionRef("arrow")
        library("fx-coroutines", "io.arrow-kt", "arrow-fx-coroutines").versionRef("arrow")
        library("fx-stm", "io.arrow-kt", "arrow-fx-stm").versionRef("arrow")
        library("optics", "io.arrow-kt", "arrow-optics").versionRef("arrow")
        library("optics-compose", "io.arrow-kt", "arrow-optics-compose").versionRef("arrow")
        library("optics-ksp-plugin", "io.arrow-kt", "arrow-optics-ksp-plugin").versionRef("arrow")
        library("optics-reflect", "io.arrow-kt", "arrow-optics-reflect").versionRef("arrow")
        library("resilience", "io.arrow-kt", "arrow-resilience").versionRef("arrow")
        library("resilience-ktor-client", "io.arrow-kt", "arrow-resilience-ktor-client").versionRef("arrow")
        library("suspendapp", "io.arrow-kt", "suspendapp").versionRef("arrow")
        library("suspendapp-ktor", "io.arrow-kt", "suspendapp-ktor").versionRef("arrow")
    }
}
