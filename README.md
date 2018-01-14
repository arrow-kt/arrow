<img height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Build Status](https://travis-ci.org/arrow-kt/arrow.svg?branch=master)](https://travis-ci.org/arrow-kt/arrow/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.2.0-blue.svg)](http://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Λrrow is a library for Typed Functional Programming in Kotlin.
It includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` and many more empowering users to define pure FP apps and libraries built atop higher order abstractions. Use the below list to learn more about Λrrow's main features.

- [Documentation](http://arrow-kt.io)
- [Patterns](http://arrow-kt.io/docs/patterns/glossary/): tutorials and approaches to day-to-day challenges using FP 
- [Type classes](http://arrow-kt.io/docs/typeclasses/functor/): defining behaviors for data
- [Data types](http://arrow-kt.io/docs/datatypes/option/): common abstractions
- [Effects](http://arrow-kt.io/docs/effects/io/): interfacing with external systems
- [Optics](http://arrow-kt.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Projects and Examples](http://arrow-kt.io/docs/quickstart/projects/)
- [Blogs and Presentations](http://arrow-kt.io/docs/quickstart/blogs/)

# Basic Setup

Add the dependencies into the project's `build.gradle`

```groovy
dependencies {
    compile 'io.arrow-kt:arrow-core:0.5.5'
    compile 'io.arrow-kt:arrow-typeclasses:0.5.5' 
    compile 'io.arrow-kt:arrow-instances:0.5.5' 
    kapt    'io.arrow-kt:arrow-annotations-processor:0.5.5' 
    
    compile 'io.arrow-kt:arrow-free:0.5.5' //optional
    compile 'io.arrow-kt:arrow-mtl:0.5.5' //optional
    compile 'io.arrow-kt:arrow-effects:0.5.5' //optional
    compile 'io.arrow-kt:arrow-effects-rx2:0.5.5' //optional
    compile 'io.arrow-kt:arrow-optics:0.5.5' //optional
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
    kapt    'io.arrow-kt:arrow-annotations-processor:0.5.5' //optional
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

# License

    Copyright (C) 2017 The Arrow Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

