---
layout: docs
title: Quick Start
permalink: /docs/
---

[![Download](https://api.bintray.com/packages/kategory/maven/kategory/images/download.svg)](https://bintray.com/kategory/maven/kategory/_latestVersion)
[![Build Status](https://travis-ci.org/kategory/kategory.svg?branch=master)](https://travis-ci.org/kategory/kategory/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.0-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

KΛTEGORY is a library for Typed Functional Programming in Kotlin.
It includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` and many more empowering users to define pure FP apps and libraries built atop higher order abstractions.
Use the list below to learn more about KΛTEGORY's main features.

- [Patterns](http://kategory.io/docs/patterns/glossary/): tutorials and approaches to day-to-day challenges using FP 
- [Type classes](http://kategory.io/docs/typeclasses/functor/): defining behaviors for data
- [Data types](http://kategory.io/docs/datatypes/option/): common abstractions
- [Effects](http://kategory.io/docs/effects/io/): interfacing with external systems
- [Optics](http://kategory.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Projects and Examples](http://kategory.io/docs/quickstart/projects/)
- [Blogs and Presentations](http://kategory.io/docs/quickstart/blogs/)

# Basic Setup

Add it in your root `build.gradle` at the end of repositories.

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url 'https://dl.bintray.com/kategory/maven' }
    }
}
```

Add the dependencies into the project's `build.gradle`

```groovy
dependencies {
    compile 'io.kategory:kategory:0.3.10'
    kapt    'io.kategory:kategory-annotations-processor:0.3.10' //optional
    compile 'io.kategory:kategory-effects:0.3.10' //optional
    compile 'io.kategory:kategory-optics:0.3.10' //optional
}
```

# Additional Setup

For projects that wish to use their own `@higherkind`, `@deriving` and other meta programming facilities provided by KΛTEGORY
the setup below is also required:

Add the dependencies into the project's `build.gradle`

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //optional

dependencies {
    ...
    kapt    'io.kategory:kategory-annotations-processor:0.3.10' //optional
    ...
}
```

JVM projects:

`gradle/generated-kotlin-sources.gradle`
```groovy
apply plugin: 'idea'

idea {
    module {
        sourceDirs += files(
            'build/generated/source/kapt/main',
            'build/generated/source/kaptKotlin/main',
            'build/tmp/kapt/main/kotlinGenerated')
        generatedSourceDirs += files(
            'build/generated/source/kapt/main',
            'build/generated/source/kaptKotlin/main',
            'build/tmp/kapt/main/kotlinGenerated')
    }
}
```

Android projects:

`build.gradle`
```groovy
sourceSets {
    main.java.srcDirs += 'src/main/kotlin'
    debug.java.srcDirs += 'build/generated/source/kaptKotlin/debug'
    release.java.srcDirs += 'build/generated/source/kaptKotlin/release'
}
```

`gradle/generated-kotlin-sources.gradle`
```groovy
apply plugin: 'idea'

idea {
    module {
        sourceDirs += files(
                'build/generated/source/kapt/main',
                'build/generated/source/kapt/debug',
                'build/generated/source/kapt/release',
                'build/generated/source/kaptKotlin/main',
                'build/generated/source/kaptKotlin/debug',
                'build/generated/source/kaptKotlin/release',
                'build/tmp/kapt/main/kotlinGenerated')
        generatedSourceDirs += files(
                'build/generated/source/kapt/main',
                'build/generated/source/kapt/debug',
                'build/generated/source/kapt/release',
                'build/generated/source/kaptKotlin/main',
                'build/generated/source/kaptKotlin/debug',
                'build/generated/source/kaptKotlin/release',
                'build/tmp/kapt/main/kotlinGenerated')
    }
}
```
