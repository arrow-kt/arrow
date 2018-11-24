---
layout: docs
title: Quick Start
permalink: /docs/
---

NOTE: The docs are currently at around 60% completion. They're the present priority project, and you can track the progress on the github issue [#311](https://github.com/arrow-kt/arrow/issues/311).

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
        jcenter()
    }
}
```

Add the dependencies into the project's `build.gradle`

```groovy
def arrow_version = "0.8.1"
dependencies {
    compile "io.arrow-kt:arrow-core:$arrow_version"
    compile "io.arrow-kt:arrow-syntax:$arrow_version"
    compile "io.arrow-kt:arrow-typeclasses:$arrow_version"
    compile "io.arrow-kt:arrow-data:$arrow_version"
    compile "io.arrow-kt:arrow-instances-core:$arrow_version"
    compile "io.arrow-kt:arrow-instances-data:$arrow_version"
    kapt    "io.arrow-kt:arrow-annotations-processor:$arrow_version"

    compile "io.arrow-kt:arrow-free:$arrow_version" //optional
    compile "io.arrow-kt:arrow-instances-free:$arrow_version" //optional
    compile "io.arrow-kt:arrow-mtl:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-instances:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-rx2:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-rx2-instances:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-reactor:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-reactor-instances:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-kotlinx-coroutines:$arrow_version" //optional
    compile "io.arrow-kt:arrow-effects-kotlinx-coroutines-instances:$arrow_version" //optional
    compile "io.arrow-kt:arrow-optics:$arrow_version" //optional
    compile "io.arrow-kt:arrow-generic:$arrow_version" //optional
    compile "io.arrow-kt:arrow-recursion:$arrow_version" //optional
    compile "io.arrow-kt:arrow-instances-recursion:$arrow_version" //optional
    compile "io.arrow-kt:arrow-integration-retrofit-adapter:$arrow_version" //optional
}
```

## Additional Setup

For projects that wish to use their own `@higherkind`, `@optics` and other meta programming facilities provided by Λrrow
the setup below is also required:

Add the dependencies into the project's `build.gradle`

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //only for Android projects

def arrow_version = "0.8.1"
dependencies {
    ...
    kapt    'io.arrow-kt:arrow-annotations-processor:$arrow_version' //optional
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
    <arrow.version>0.8.1</arrow.version>
</properties>
```

Add the dependencies that you want to use
```
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
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
    <artifactId>arrow-data</artifactId>
    <version>${arrow.version}</version>
</dependency>
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-instances-core</artifactId>
    <version>${arrow.version}</version>
</dependency>
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-instances-data</artifactId>
    <version>${arrow.version}</version>
</dependency>
```

## Enabling kapt

Add to your pom.xml file the following repository:
```
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

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
                        <artifactId>arrow-annotations-processor</artifactId>
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
