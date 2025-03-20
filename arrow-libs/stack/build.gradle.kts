plugins {
  `java-platform`
  alias(libs.plugins.publish)
}

group = property("projects.group").toString()

dependencies {
  constraints {
    api("io.arrow-kt:arrow-annotations:$version")
    api("io.arrow-kt:arrow-atomic:$version")
    api("io.arrow-kt:arrow-autoclose:$version")
    api("io.arrow-kt:arrow-cache4k:$version")
    api("io.arrow-kt:arrow-core:$version")
    api("io.arrow-kt:arrow-core-high-arity:$version")
    api("io.arrow-kt:arrow-eval:$version")
    api("io.arrow-kt:arrow-functions:$version")
    api("io.arrow-kt:arrow-platform:${version}")
    api("io.arrow-kt:arrow-collectors:$version")
    api("io.arrow-kt:arrow-fx-coroutines:$version")
    api("io.arrow-kt:arrow-fx-stm:$version")
    api("io.arrow-kt:arrow-resilience:$version")
    api("io.arrow-kt:arrow-optics:$version")
    api("io.arrow-kt:arrow-optics-compose:$version")
    api("io.arrow-kt:arrow-optics-reflect:$version")
    api("io.arrow-kt:arrow-optics-ksp-plugin:$version")
    api("io.arrow-kt:arrow-core-jackson:$version")
    api("io.arrow-kt:arrow-core-retrofit:$version")
    api("io.arrow-kt:arrow-core-serialization:$version")
    // api("io.arrow-kt:arrow-raise-ktor-server:${version}")
    api("io.arrow-kt:arrow-resilience-ktor-client:${version}")
    api("io.arrow-kt:suspendapp:${version}")
    api("io.arrow-kt:suspendapp-ktor:${version}")
  }
}
