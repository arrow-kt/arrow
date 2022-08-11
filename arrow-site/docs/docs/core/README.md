---
layout: quickstart-core
title: Core
permalink: /core/
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

# Arrow Core. Functional companion to Kotlin’s Standard Library

Arrow Core includes types such as [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' |
relative_url }}) and many extensions to [`Iterable`]({{ '/apidocs/arrow-core/arrow.core/index.html#functions' |
relative_url }}) that can be used when implementing [error handling patterns]({{ '/patterns/error_handling/' | relative_url }}).

Core also includes the base continuation effects system, which includes patterns to remove callbacks
and enables controlled effects in direct syntax. Some applications of the effect system reduce
boilerplate and enable direct syntax including [monad comprehensions and computation expressions](
{{ '/patterns/monad_comprehensions/' | relative_url }}).
</div>

<div class="quickstart-intro" markdown="1">
## Arrow Core Overview

<div class="quickstart-coroutines-list" markdown="1">

<div class="quickstart-coroutines-item" markdown="1">
#### Quick Start
  - [Gradle Setup]({{ '/core/#Gradle-kotlin' | relative_url }})
  - [Maven Setup]({{ '/core/#Maven' | relative_url }})

#### Effects & Continuations
  - [Effect]({{ '/apidocs/arrow-core/arrow.core.continuations/-effect/' | relative_url }})
  - [EffectScope]({{ '/apidocs/arrow-core/arrow.core.continuations/-effect-scope/' | relative_url }})
  - [EagerEffect]({{ '/apidocs/arrow-core/arrow.core.continuations/-eager-effect/' | relative_url }})
  - [EagerEffectScope]({{ '/apidocs/arrow-core/arrow.core.continuations/-eager-effect-scope/' | relative_url }})

</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Extensions and data types
  - [Either]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }})
  - [Validated]({{ '/apidocs/arrow-core/arrow.core/-validated/' | relative_url }})
  - [Option]({{ '/apidocs/arrow-core/arrow.core/-option/' | relative_url }})
  - [NonEmptyList]({{ '/apidocs/arrow-core/arrow.core/-non-empty-list/' | relative_url }})
  - [Ior]({{ '/apidocs/arrow-core/arrow.core/-ior/' | relative_url }})
  - [Eval]({{ '/apidocs/arrow-core/arrow.core/-eval/' | relative_url }})
  - [Monoid]({{ '/arrow/typeclasses/monoid/' | relative_url }})
  - [Semiring]({{ '/apidocs/arrow-core/arrow.typeclasses/-semiring/' | relative_url }})
  - [Extensions]({{ '/apidocs/arrow-core/arrow.core/index.html#functions' | relative_url }})

</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Tutorials 
  - [Error Handling]({{ '/patterns/error_handling/' | relative_url }})
  - [Monads]({{ '/patterns/monads/' | relative_url }})
  - [Monad Comprehensions]({{ '/patterns/monad_comprehensions/' | relative_url }})
  - [Coroutines & Computation blocks]({{ '/fx/coroutines/' | relative_url }})

</div>

<div class="quickstart-coroutines-item" markdown="1">
#### Additional information
  - [Kotlin's Std Lib Guide](https://kotlinlang.org/api/latest/jvm/stdlib/)
  - [Pure & Referentially Transparent Functions]({{ '/fx/purity-and-referentially-transparent-functions/' | relative_url }})
  - [Why suspend over IO monad]({{ '/effects/io/' | relative_url }})
  - [Semantics of Structured Concurrency and Effect]({{ '/arrow/core/continuations/' | relative_url }})
</div>
</div>
</div>

<div class="setup" markdown="1">
## Setup

{: .setup-subtitle}
Configure Arrow for your project
<div class="setup-jdk-android" markdown="1">
<div class="jdk-item" markdown="1">
![Jdk]({{ "/img/quickstart/jdk-logo.svg" | relative_url }} "jdk")

Make sure to have the latest version of JDK 1.8 installed.
</div>
<div class="android-item" markdown="1">
![Android]({{ "/img/quickstart/android-logo.svg" | relative_url }} "android")

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
    implementation("io.arrow-kt:arrow-core:1.0.1")
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation("io.arrow-kt:arrow-core")
    ...
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with on of the latest `alpha`, `beta` or `rc` publications.

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

##### Arrow Core

```groovy
def arrow_version = "1.0.1"
dependencies {
    implementation "io.arrow-kt:arrow-core:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
def arrow_version = "1.0.1"
dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-core"
    ...
}
```

#### Next development version

If you want to try the latest features, replace `1.0.1` with one of the latest `alpha`, `beta` or `rc` publications.

</div>

<div id="Maven" class="tabcontent" markdown="1">

#### Basic Setup

Make sure to have at least the latest version of JDK 1.8 installed. Add to your pom.xml file the
following properties:

```xml

<properties>
    <kotlin.version>1.6.10</kotlin.version>
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

If you want to try the latest features, replace `1.0.1` with one of the latest `alpha`, `beta` or `rc` publications.

</div>
</div>

</div>
