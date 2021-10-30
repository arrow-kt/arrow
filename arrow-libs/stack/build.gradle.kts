plugins {
    `java-platform`
    alias(libs.plugins.arrowGradleConfig.publishJavaPlatform)
}

group = property("projects.group").toString()
version = property("projects.version").toString()

dependencies {
    constraints {
        api("io.arrow-kt:arrow-annotations:$version")
        api("io.arrow-kt:arrow-continuations:$version")
        api("io.arrow-kt:arrow-core:$version")
        api("io.arrow-kt:arrow-core-retrofit:$version")
        api("io.arrow-kt:arrow-core-test:$version")
        api("io.arrow-kt:arrow-fx-coroutines:$version")
        api("io.arrow-kt:arrow-fx-coroutines-test:$version")
        api("io.arrow-kt:arrow-fx-stm:$version")
        api("io.arrow-kt:arrow-meta:$version")
        api("io.arrow-kt:arrow-optics:$version")
        api("io.arrow-kt:arrow-optics-test:$version")
    }
}
