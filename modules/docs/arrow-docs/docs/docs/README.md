---
layout: docs
title: Quick Start
permalink: /docs/
---

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Build Status](https://travis-ci.org/arrow-kt/arrow.svg?branch=master)](https://travis-ci.org/arrow-kt/arrow/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow aims to provide a [*lingua franca*](https://en.wikipedia.org/wiki/Lingua_franca) of interfaces and abstractions across Kotlin libraries.
For this, it includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` to empower users to write pure FP apps and libraries built atop higher order abstractions.

Use the list below to learn more about Λrrow's main features.

- [Patterns](http://arrow-kt.io/docs/patterns/glossary/): tutorials and approaches to day-to-day challenges using FP
- [Libraries](http://arrow-kt.io/docs/quickstart/libraries/): all the libraries provided by Λrrow
- [Type classes](http://arrow-kt.io/docs/typeclasses/intro/): defining behaviors for data
- [Data types](http://arrow-kt.io/docs/datatypes/intro/): common abstractions
- [Effects](http://arrow-kt.io/docs/effects/io/): interfacing with external systems
- [Optics](http://arrow-kt.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Projects and Examples](http://arrow-kt.io/docs/quickstart/projects/)
- [Blogs and Presentations](http://arrow-kt.io/docs/quickstart/blogs/)

# Gradle 
## Basic Setup

Make sure to have the latest version of JDK 1.8 installed.

In your project's root `build.gradle` append the jcenter repository to your list of repositories.

```groovy
allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { url "https://dl.bintray.com/arrow-kt/arrow-kt/" }
        maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' }
    }
}
```

# Dependency breakdown starting in Arrow 0.9.1

Starting in 0.9.1, Arrow follows the following convention for artifact publication.

The arrow modules are Core, Effects, Optics, Recursion, etc.

An Arrow module is composed of data types and type classes.
Arrow modules are exported and published with the following semantics.

If we take for example `arrow-core`.

Arrow core contains the basic arrow type classes and data types and it's composed of 3 main artifacts that may be used a la carte:

Recomended for most use cases:

- `arrow-core` (Depends on data and extensions modules and exports both)

Trimmed down versions:

- `arrow-core-data` (Only data types)
- `arrow-core-extensions` (Only type class extensions)

# Current stable version 0.9.0

```groovy
def arrow_version = "0.9.0"
```

You can find the dependencies necessary in the Basic Setup of the README at the 0.9.0 tag clicking [here](https://github.com/arrow-kt/arrow/blob/0.9.0/README.md#next-development-version-090).

# Next development version 0.9.1

Add the dependencies into the project's `build.gradle`

```groovy
def arrow_version = "0.9.1-SNAPSHOT"
dependencies {
    compile "io.arrow-kt:arrow-core-data:$arrow_version"
    compile "io.arrow-kt:arrow-core-extensions:$arrow_version"
    compile "io.arrow-kt:arrow-syntax:$arrow_version"
    compile "io.arrow-kt:arrow-typeclasses:$arrow_version"
    compile "io.arrow-kt:arrow-extras-data:$arrow_version"
    compile "io.arrow-kt:arrow-extras-extensions:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"

    compile "io.arrow-kt:arrow-query-language:$arrow_version" //optional
    compile "io.arrow-kt:arrow-free-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-free-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-mtl:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-io-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-rx2-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-rx2-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-reactor-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-reactor-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-kotlinx-coroutines-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-kotlinx-coroutines-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-optics:$arrow_version" //optional
    compile "io.arrow-kt:arrow-generic:$arrow_version" //optional
    compile "io.arrow-kt:arrow-recursion-data:$arrow_version" //optional
    compile "io.arrow-kt:arrow-recursion-extensions:$arrow_version" //optional
    compile "io.arrow-kt:arrow-query-language:$arrow_version" //optional
    compile "io.arrow-kt:arrow-integration-retrofit-adapter:$arrow_version" //optional
}
```

# Additional Setup

For projects that wish to use their own `@higherkind`, `@optics` and other meta programming facilities provided by Λrrow
the setup below is also required:

Add the dependencies into the project's `build.gradle`

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //only for Android projects

def arrow_version = "0.9.0"
dependencies {
    ...
    kapt    'io.arrow-kt:arrow-meta:$arrow_version' //optional
    ...
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
# Maven 
## Basic Setup

Make sure to have the at least the latest version of JDK 1.8 installed.
Add to your pom.xml file the following properties:
```
<properties>
    <kotlin.version>1.3.0</kotlin.version>
    <arrow.version>0.8.2</arrow.version>
</properties>
```

Add the dependencies that you want to use
```
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core-extensions</artifactId>
    <version>${arrow.version}</version>
</dependency>
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-syntax</artifactId>
    <version>${arrow.version}</version>
</dependency>
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-typeclasses</artifactId>
    <version>${arrow.version}</version>
</dependency>
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-extras-extensions</artifactId>
    <version>${arrow.version}</version>
</dependency>
```

## Enabling kapt

Enable annotaton processing using kotlin plugin 
```
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
    <executions>
        <execution>
            <id>kapt</id>
            <goals>
                <goal>kapt</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>src/main/kotlin</sourceDir>
                </sourceDirs>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>io.arrow-kt</groupId>
                        <artifactId>arrow-meta</artifactId>
                        <version>${arrow.version}</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </execution>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>src/main/kotlin</sourceDir>
                </sourceDirs>
            </configuration>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
