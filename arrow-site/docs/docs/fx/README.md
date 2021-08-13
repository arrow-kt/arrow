---
layout: docs-fx
title: Arrow Fx. Async and Concurrent Functional Programming for Kotlin
permalink: /fx/
---

# Arrow Fx. Typed FP for the masses

Arrow Fx is a next-generation Typed FP Effects Library that makes tracked effectful programming first class in Kotlin built on top of Kotlin's suspend system and [KotlinX Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
Arrow Fx is a functional companion to KotlinX Coroutines augmenting its api with well known functional operators making it easier to compose async and concurrent programs.

The library brings purity, referential transparency, and direct imperative syntax to typed FP in Kotlin, and is a fun and easy tool for creating Typed Pure Functional Programs.

If you're not familiar yet with Coroutines in Kotlin, it's recommended to first read Kotlin's [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) on KotlinX Coroutines.

## Arrow Fx Coroutines Overview 

- Quick Start
  - [Gradle Setup](/docs/fx/#gradle-setup)
  - [Maven Setup](/docs/fx/#maven-setup)
- Extensions and data types
  - [Parallel](/docs/fx/parallel/)
  - [Schedule](/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-schedule/)
  - [Resource](/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-resource/)
  - [CircuitBreaker](/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/)
  - [Atomic](/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-atomic/)
  - [STM](/docs/apidocs/arrow-fx-stm/arrow.fx.stm/-s-t-m/)
- [Integrating with 3rd-party libraries](/docs/fx/#integrating-with-3rd-party-libraries)
- Additional Information
  - [Kotlin's Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
  - [Pure & Referentially Transparent Functions](/docs/fx/purity-and-referentially-transparent-functions/)
  - [Kotlin's Std Coroutines package](/docs/fx/coroutines/)
  - [Why suspend over IO monad](/docs/effects/io/)

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

```kotlin
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependencies into the project's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.arrow-kt:arrow-fx-coroutines:0.13.2")
    implementation("io.arrow-kt:arrow-fx-stm:0.13.2")
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```kotlin
dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:0.13.2"))

    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
}
```

#### Next development version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this configuration:

```kotlin
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
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
    implementation "io.arrow-kt:arrow-fx-stm:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-fx-coroutines"
    implementation "io.arrow-kt:arrow-fx-stm"
}
```

#### Next development version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this configuration:

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

Make sure to have at least the latest version of JDK 1.8 installed.
Add to your pom.xml file the following properties:

```xml
<properties>
    <kotlin.version>1.5.20</kotlin.version>
    <arrow.version>0.13.2</arrow.version>
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
  </dependencyManagement>
  <dependencies>
    ...
  </dependencies>
```

#### Next development version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this configuration:

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


</div>

## Integrating with 3rd-party libraries

Arrow Fx integrates with KotlinX Coroutines Fx, Reactor framework, and any library that can model effectful async/concurrent computations as `suspend`.

If you are interested in the Arrow Fx library, please contact us in the #Arrow channel on the official [Kotlin Lang Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) with any questions and we'll help you along the way.
