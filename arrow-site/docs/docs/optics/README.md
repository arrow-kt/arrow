---
layout: quickstart-optics
title: Optics
permalink: /optics/
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

# Arrow Optics. Transforming and computing over immutable data models in Kotlin

Arrow Optics provides an automatic DSL that allows users to use `.` notation when accessing,
composing, and transforming deeply nested immutable data structures.

Optics also offers all the base types such as [Lens]({{ "/optics/lens/" | relative_url }}), [Prism](
{{ '/optics/prism/' | relative_url }}), and others from which we can generalize accessing and
traversing deep values in sealed and data classes models.s
</div>

<div class="quickstart-intro" markdown="1">
## Arrow Optics Overview

<div class="quickstart-coroutines-list" markdown="1">

<div class="quickstart-coroutines-item" markdown="1">
#### Quick Start
  - [Gradle Setup]({{ '/optics/#Gradle-kotlin' | relative_url }})
  - [Maven Setup]({{ '/optics/#Maven' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### DSL 
  - [Optics DSL]({{ '/optics/dsl/' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Extensions and data types
  - [Iso]({{ '/optics/iso/' | relative_url }})
  - [Lens]({{ '/optics/lens/' | relative_url }})
  - [Optional]({{ '/optics/optional/' | relative_url }})
  - [Prism]({{ '/optics/prims/' | relative_url }})
  - [Getter]({{ '/optics/getter/' | relative_url }})
  - [Setter]({{ '/optics/setter/' | relative_url }})
  - [Fold]({{ '/optics/fold/' | relative_url }})
  - [Traversal]({{ '/optics/traversal/' | relative_url }})
  - [Every]({{ '/optics/every/' | relative_url }})
  - [Cons]({{ '/optics/cons/' | relative_url }})
  - [Snoc]({{ '/optics/snoc/' | relative_url }})
  - [At]({{ '/optics/at/' | relative_url }})
  - [Index]({{ '/optics/index/' | relative_url }})
  - [FilterIndex]({{ '/optics/filterindex/' | relative_url }})
</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Additional information
  - [Kotlin Data classes](https://kotlinlang.org/docs/data-classes.html)
  - [Kotlin Sealed classes](https://kotlinlang.org/docs/sealed-classes.html)
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
apply plugin: 'kotlin-kapt'

dependencies {
    implementation("io.arrow-kt:arrow-optics:1.0.1")
    kapt("io.arrow-kt:arrow-meta:1.0.1")
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    ...
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with `1.0.2-SNAPSHOT` and add this
configuration:

```
allprojects {
    repositories {
        ...
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
apply plugin: 'kotlin-kapt'

def arrow_version = "1.0.1"
dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
def arrow_version = "1.0.1"
dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-core"
    implementation "io.arrow-kt:arrow-optics"
    ...
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with `1.0.2-SNAPSHOT` and add this
configuration:

```groovy
allprojects {
    repositories {
        ...
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


</div>
