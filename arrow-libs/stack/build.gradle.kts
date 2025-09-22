plugins {
  `java-platform`
  id(libs.plugins.publish.get().pluginId)
}

mavenPublishing {
  configureBasedOnAppliedPlugins()
  pomFromGradleProperties()
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}

group = property("projects.group").toString()

dependencies {
  constraints {
    api("io.arrow-kt:arrow-annotations:$version")
    api("io.arrow-kt:arrow-annotations-jvm:$version")
    api("io.arrow-kt:arrow-atomic:$version")
    api("io.arrow-kt:arrow-atomic-jvm:$version")
    api("io.arrow-kt:arrow-autoclose:$version")
    api("io.arrow-kt:arrow-autoclose-jvm:$version")
    api("io.arrow-kt:arrow-cache4k:$version")
    api("io.arrow-kt:arrow-cache4k-jvm:$version")
    api("io.arrow-kt:arrow-core:$version")
    api("io.arrow-kt:arrow-core-jvm:$version")
    api("io.arrow-kt:arrow-core-high-arity:$version")
    api("io.arrow-kt:arrow-core-high-arity-jvm:$version")
    api("io.arrow-kt:arrow-eval:$version")
    api("io.arrow-kt:arrow-eval-jvm:$version")
    api("io.arrow-kt:arrow-functions:$version")
    api("io.arrow-kt:arrow-functions-jvm:$version")
    api("io.arrow-kt:arrow-platform:${version}")
    api("io.arrow-kt:arrow-platform-jvm:${version}")
    api("io.arrow-kt:arrow-collectors:$version")
    api("io.arrow-kt:arrow-collectors-jvm:$version")
    api("io.arrow-kt:arrow-fx-coroutines:$version")
    api("io.arrow-kt:arrow-fx-coroutines-jvm:$version")
    api("io.arrow-kt:arrow-fx-stm:$version")
    api("io.arrow-kt:arrow-fx-stm-jvm:$version")
    api("io.arrow-kt:arrow-resilience:$version")
    api("io.arrow-kt:arrow-resilience-jvm:$version")
    api("io.arrow-kt:arrow-optics:$version")
    api("io.arrow-kt:arrow-optics-jvm:$version")
    api("io.arrow-kt:arrow-optics-compose:$version")
    api("io.arrow-kt:arrow-optics-compose-jvm:$version")
    api("io.arrow-kt:arrow-optics-reflect:$version")
    api("io.arrow-kt:arrow-optics-ksp-plugin:$version")
    api("io.arrow-kt:arrow-core-jackson:$version")
    api("io.arrow-kt:arrow-core-retrofit:$version")
    api("io.arrow-kt:arrow-core-serialization:$version")
    api("io.arrow-kt:arrow-core-serialization-jvm:$version")
    // api("io.arrow-kt:arrow-raise-ktor-server:${version}")
    api("io.arrow-kt:arrow-resilience-ktor-client:${version}")
    api("io.arrow-kt:arrow-resilience-ktor-client-jvm:${version}")
    api("io.arrow-kt:suspendapp:${version}")
    api("io.arrow-kt:suspendapp-jvm:${version}")
    api("io.arrow-kt:suspendapp-ktor:${version}")
    api("io.arrow-kt:suspendapp-ktor-jvm:${version}")
  }
}
