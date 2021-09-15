---
layout: docs-core
title: Quick Start
permalink: /core/
---

[![Maven Central](https://img.shields.io/maven-central/v/io.arrow-kt/arrow-core?color=4caf50&label=latest%20release)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Latest snapshot](https://img.shields.io/badge/dynamic/xml?color=orange&label=latest%20snapshot&prefix=v&query=%2F%2Fmetadata%2Fversioning%2Flatest&url=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-core%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/)
[![Kotlin version](https://img.shields.io/badge/Kotlin-1.4-blue)](https://kotlinlang.org/docs/reference/whatsnew14.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-grey.svg?logo=stackoverflow)](https://stackoverflow.com/questions/tagged/arrow-kt)
[![Twitter](https://img.shields.io/twitter/follow/arrow_kt?color=blue&style=flat)](https://twitter.com/arrow_kt)

Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow complements the Kotlin standard library and KotlinX Coroutines libraries with functional patterns that favor composition, effect control, and immutability.

Arrow is composed of 4 main modular libraries:

* [**Core**]({{ '/core/' | relative_url }}): Arrow Core includes types such as [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }}), [`Validated`]({{ '/apidocs/arrow-core/arrow.core/-validated/' | relative_url }}) and many extensions to [`Iterable`]({{ '/apidocs/arrow-core/arrow.core/kotlin.collections.-iterable/' | relative_url }}) that can be used when implementing [error handling patterns]({{ '/patterns/error_handling/' | relative_url }}).
Core also includes the base continuation effects system, which includes patterns to remove callbacks and enables controlled effects in direct syntax. Some applications of the effect system reduce boilerplate and enable direct syntax including [monad comprehensions and computation expressions]({{ '/patterns/monad_comprehensions/' | relative_url }}).

* [**Fx**]({{ '/fx/' | relative_url }}): Arrow Fx is a full-featured, high-performance, asynchronous framework that brings functional operators to Kotlin's `suspend` functions.
By leveraging the power of KotlinX Coroutines and the compiler support for CPS transformations, Arrow Fx results in optimal async programs with increased throughput and decreased allocations.

* [**Optics**]({{ '/optics/dsl/' | relative_url }}): Arrow Optics provides an automatic DSL that allows users to use `.` notation when accessing, composing, and transforming deeply nested immutable data structures.
Optics also offers all the base types such as [Lens]({{ '/optics/lens/' | relative_url }}), [Prism]({{ '/optics/prism/' | relative_url }}), and others from which we can generalize accessing and traversing deep values in sealed and data classes models.

* [**Meta**](https://meta.arrow-kt.io): Arrow Meta is a general purpose library for meta-programming in Kotlin to build compiler plugins.
Some type system features proposed by Arrow such as union types, product types, proof derivation, and others are built with Arrow Meta and serve as examples of what could be incorporated in the Kotlin compiler.

## Setup

### JDK

Make sure to have the latest version of JDK 1.8 installed.

### Android

Arrow supports Android starting on API 21 and up.

### Gradle

#### Basic Setup

In your project's root `build.gradle`, append this repository to your list:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependencies into the project's `build.gradle`:

##### Arrow Core

```groovy
def arrow_version = "1.0.0"
dependencies {
    implementation "io.arrow-kt:arrow-core:$arrow_version"
}
```

##### Arrow Core + Arrow Optics

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "1.0.0"
dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Arrow Core + Arrow Fx

```groovy
def arrow_version = "1.0.0"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-core"
    implementation "io.arrow-kt:arrow-fx-coroutines"
    ...
```

### Maven

#### Basic Setup

Make sure to have at least the latest version of JDK 1.8 installed.
Add to your pom.xml file the following properties:
```
<properties>
    <kotlin.version>1.4.0</kotlin.version>
    <arrow.version>1.0.0</arrow.version>
</properties>
```

Add the dependencies that you want to use:
```
        <dependency>
            <groupId>io.arrow-kt</groupId>
            <artifactId>arrow-core</artifactId>
            <version>${arrow.version}</version>
        </dependency>

```

#### Enabling kapt for the Optics DSL

For the Optics DSL, enable annotation processing using Kotlin plugin:
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

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>io.arrow-kt</groupId>
        <artifactId>arrow-stack</artifactId>
        <version>${arrow.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    ...
  </dependencies>
```

## Next development version

If you want to try the latest features, replace `1.0.0` with `1.0.1-SNAPSHOT` and add this repository:

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
}
```
