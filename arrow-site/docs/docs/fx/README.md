---
layout: quickstart-fx
title: Arrow Fx. Async and Concurrent Functional Programming for Kotlin
permalink: /fx/
---

<div class="quick-snap" markdown="1">
[![Maven Central](https://img.shields.io/maven-central/v/io.arrow-kt/arrow-core?color=4caf50&label=latest%20release)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Latest snapshot](https://img.shields.io/badge/dynamic/xml?color=orange&label=latest%20snapshot&prefix=v&query=%2F%2Fmetadata%2Fversioning%2Flatest&url=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-core%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/)
[![Kotlin version](https://img.shields.io/badge/Kotlin-1.4-blue)](https://kotlinlang.org/docs/reference/whatsnew14.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-grey.svg?logo=stackoverflow)](https://stackoverflow.com/questions/tagged/arrow-kt)
[![Twitter](https://img.shields.io/twitter/follow/arrow_kt?color=blue&style=flat)](https://twitter.com/arrow_kt)
</div>

<div class="quickstart-doc" markdown="1">
<div class="quickstart-intro" markdown="1">

# Arrow Fx. Typed FP for the masses

Arrow Fx is a next-generation Typed FP Effects Library that makes tracked effectful programming
first class in Kotlin built on top of Kotlin's suspend system
and [KotlinX Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
Arrow Fx is a functional companion to KotlinX Coroutines augmenting its api with well known
functional operators making it easier to compose async and concurrent programs.

The library brings purity, referential transparency, and direct imperative syntax to typed FP in
Kotlin, and is a fun and easy tool for creating Typed Pure Functional Programs.

If you're not familiar yet with Coroutines in Kotlin, it's recommended to first read Kotlin'
s [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) on KotlinX Coroutines.
</div>

<div class="quickstart-intro" markdown="1">
## Arrow Fx Coroutines Overview

<div class="quickstart-coroutines-list" markdown="1">

<div class="quickstart-coroutines-item" markdown="1">
#### Quick Start
  - [Gradle Setup]({{ '/fx/#Gradle-kotlin' | relative_url }})
  - [Maven Setup]({{ '/fx/#Maven' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Extensions and data types
  - [Parallel]({{ '/fx/parallel' | relative_url }})
  - [Schedule]({{ '/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-schedule/' | relative_url }})
  - [Resource]({{ '/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-resource/' | relative_url }})
  - [CircuitBreaker]({{ '/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker' | relative_url }})
  - [Atomic]({{ '/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-atomic/' | relative_url }})
  - [STM]({{ '/apidocs/arrow-fx-stm/arrow.fx.stm/-s-t-m/' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Integrating 
  - [Integrating with 3rd-party libraries]({{ '/fx/#integrating-with-3rd-party-libraries' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Additional Information
  - [Kotlin's Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
  - [Pure & Referentially Transparent Functions]({{ '/fx/purity-and-referentially-transparent-functions/' | relative_url }})
  - [Kotlin's Std Coroutines package]({{ '/fx/coroutines/' | relative_url }})
  - [Why suspend over IO monad]({{ '/effects/io/' | relative_url }})
</div>
</div>
</div>

<!--- Setup
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-->

<div class="setup" markdown="1">
## Setup

{: .setup-subtitle}
Configure Arrow for your project
<div class="setup-jdk-android" markdown="1">
<div class="jdk-item" markdown="1">
![Jdk](/img/quickstart/jdk-logo.svg "jdk")

Make sure to have the latest version of JDK 1.8 installed.
</div>
<div class="android-item" markdown="1">
![Android](/img/quickstart/android-logo.svg "android")

<!--- Module Libraries -->
Arrow supports Android starting on API 21 and up.
</div>
</div>

<div class="setup-graddle-maven" markdown="1">
<!-- Tab links -->
<div class="tab" markdown="1">
  <button class="tablinks" onclick="openSetup(event, 'Gradle-kotlin')" id="defaultOpen" markdown="1">Gradle Kotlin DSL</button>
  <button class="tablinks" onclick="openSetup(event, 'Gradle-Groovy')" markdown="1">Gradle Groovy DSL</button>
  <button class="tablinks" onclick="openSetup(event, 'Maven')" markdown="1">Maven</button>
</div>

<!-- Tab content -->
<div id="Gradle-kotlin" class="tabcontent" markdown="1">

#### Basic Setup

In your project's root `build.gradle.kts`, append this repository to your list:

```
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependencies into the project's `build.gradle.kts`:

```
dependencies {
    implementation("io.arrow-kt:arrow-fx-coroutines:1.0.1")
    implementation("io.arrow-kt:arrow-fx-stm:1.0.1")
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with `1.0.2-SNAPSHOT` and add this
configuration:

```
allprojects {
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }

    // To use latest artifacts
    configurations.all { resolutionStrategy.cacheChangingModulesFor(0, "seconds") }
}
```

</div>

<div id="Gradle-Groovy" class="tabcontent" markdown="1">

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

```groovy
def arrow_version = "1.0.1"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
    implementation "io.arrow-kt:arrow-fx-stm:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
def arrow_version = "1.0.1"
dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-fx-coroutines"
    implementation "io.arrow-kt:arrow-fx-stm"
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with `1.0.2-SNAPSHOT` and add this
configuration:

```groovy
allprojects {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }

    // To use latest artifacts
    configurations.all { resolutionStrategy.cacheChangingModulesFor 0, 'seconds' }
}
```

</div>

<div id="Maven" class="tabcontent" markdown="1">

#### Basic Setup

Make sure to have at least the latest version of JDK 1.8 installed. Add to your pom.xml file the
following properties:

```xml
<properties>
    <kotlin.version>1.5.31</kotlin.version>
    <arrow.version>1.0.1</arrow.version>
</properties>
```

Add the dependencies that you want to use:

```xml

<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
    <version>${arrow.version}</version>
</dependency>
```

#### Enabling kapt for the Optics DSL

For the Optics DSL, enable annotation processing using Kotlin plugin:

```xml

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

```xml

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
</dependencyManagement><dependencies>
...
</dependencies>
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with `1.0.2-SNAPSHOT` and add this
configuration:

```xml

<repository>
    <snapshotss>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

</div>
</div>

</div>

<div class="quickstart-intro" markdown="1">
## Integrating with 3rd-party libraries

Arrow Fx integrates with KotlinX Coroutines Fx, Reactor framework, and any library that can model
effectful async/concurrent computations as `suspend`.

If you are interested in the Arrow Fx library, please contact us in the #Arrow channel on the
official [Kotlin Lang Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) with any questions and
we'll help you along the way.
</div>
</div>
