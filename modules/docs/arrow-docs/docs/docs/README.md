---
layout: docs
title: Quick Start
permalink: /docs/
---

NOTE: The docs are currently at around 50% completion. They're the present priority project, and you can track the progress on the github issue [#311](https://github.com/arrow-kt/arrow/issues/311).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Build Status](https://travis-ci.org/arrow-kt/arrow.svg?branch=master)](https://travis-ci.org/arrow-kt/arrow/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.0-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Λrrow is a library for Typed Functional Programming in Kotlin.
It includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` and many more empowering users to define pure FP apps and libraries built atop higher order abstractions.
Use the list below to learn more about Λrrow's main features.

- [Patterns](http://arrow-kt.io/docs/patterns/glossary/): tutorials and approaches to day-to-day challenges using FP 
- [Type classes](http://arrow-kt.io/docs/typeclasses/intro/): defining behaviors for data
- [Data types](http://arrow-kt.io/docs/datatypes/intro/): common abstractions
- [Effects](http://arrow-kt.io/docs/effects/io/): interfacing with external systems
- [Optics](http://arrow-kt.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Projects and Examples](http://arrow-kt.io/docs/quickstart/projects/)
- [Blogs and Presentations](http://arrow-kt.io/docs/quickstart/blogs/)

# Basic Setup

Make sure to have the latest version of JDK 1.8 installed.

Add it in your root `build.gradle` at the end of repositories.

```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```

Add the dependencies into the project's `build.gradle`

```groovy
dependencies {
    compile 'io.arrow-kt:arrow-core:0.6.1'
    compile 'io.arrow-kt:arrow-typeclasses:0.6.1' 
    compile 'io.arrow-kt:arrow-instances:0.6.1' 
    compile 'io.arrow-kt:arrow-data:0.6.1' 
    compile 'io.arrow-kt:arrow-syntax:0.6.1'
    kapt    'io.arrow-kt:arrow-annotations-processor:0.6.1' 
    
    compile 'io.arrow-kt:arrow-free:0.6.1' //optional
    compile 'io.arrow-kt:arrow-mtl:0.6.1' //optional
    compile 'io.arrow-kt:arrow-effects:0.6.1' //optional
    compile 'io.arrow-kt:arrow-effects-rx2:0.6.1' //optional
    compile 'io.arrow-kt:arrow-effects-kotlinx-coroutines:0.6.1' //optional
    compile 'io.arrow-kt:arrow-optics:0.6.1' //optional
}
```

# Additional Setup

For projects that wish to use their own `@higherkind`, `@deriving` and other meta programming facilities provided by Λrrow
the setup below is also required:

Add the dependencies into the project's `build.gradle`

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //optional

dependencies {
    ...
    kapt    'io.arrow-kt:arrow-annotations-processor:0.6.1' //optional
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
