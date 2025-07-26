# Arrow Version Catalog

## settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("arrow") {
            from("io.arrow-kt:arrow-version-catalog:2.1.2")
        }
    }
}
```

## build.gradle.kts

```kotlin
dependencies {
    implementation(arrow.autoclose)
    implementation(arrow.core)
    implementation(arrow.fx.coroutines)
    etc
}
```
