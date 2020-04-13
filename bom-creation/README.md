# BOM file

Thanks to [@ps-feng](https://github.com/ps-feng) for creating [#2129](https://github.com/arrow-kt/arrow/issues/2129) proposal.

BOM file is useful to include several dependencies without the specification of their versions.

## Use

```
dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$ARROW_VERSION")

    // Versions are no longer required here
    implementation "io.arrow-kt:arrow-core"
    implementation "io.arrow-kt:arrow-fx"
    implementation "io.arrow-kt:arrow-mtl"
    implementation "io.arrow-kt:arrow-syntax"
    ...
}
```

## Links

* [Gradle: The Java Platform Plugin](https://docs.gradle.org/current/userguide/java_platform_plugin.html#java_platform_plugin)
* [Gradle: Sharing dependency versions between projects](https://docs.gradle.org/current/userguide/platforms.html#sub:bom_import)
