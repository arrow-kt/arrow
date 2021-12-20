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

<div id="setup" class="setup" markdown="1">
## Setup

<div class="setup-graddle-maven" markdown="1">
<!-- Tab links -->
<div class="tab" markdown="1">
  <button class="tablinks" onclick="openSetup(event, 'Gradle-kotlin')" id="defaultOpen" markdown="1">Gradle Kotlin DSL</button>
  <button class="tablinks" onclick="openSetup(event, 'Gradle-Groovy')" markdown="1">Gradle Groovy DSL</button>
</div>

<!-- Tab content -->
<div id="Gradle-kotlin" class="tabcontent" markdown="1">

#### Step 1: add the repository

In your project's root `build.gradle.kts`, append this repository to your list:

```
allprojects {
    repositories {
        mavenCentral()
    }
}
```

#### Step 2: add the library

Add the dependencies into the project's `build.gradle.kts`:

```
dependencies {
    implementation("io.arrow-kt:arrow-optics:1.0.1")
}
```

If you are using more than one Arrow dependency, you can avoid specifying the same version over and over by using a BOM file:

```
dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    ...
}
```

#### Step 3: add the plug-in

To get the most of Arrow Optics you can add out Kotlin plug-in to your build, which takes care of generating optics for your data types.

```
plugins {
    id("com.google.devtools.ksp") version "1.6.0-1.0.2"
}

dependencies {
    ksp("io.arrow-kt:arrow-optics-ksp:2.0-SNAPSHOT")
}
```

Now you are ready to learn about the [Optics DSL]({{ '/optics/dsl/' | relative_url }})!

</div>

<div id="Gradle-Groovy" class="tabcontent" markdown="1">

#### Step 1: add the repository

In your project's root `build.gradle`, append this repository to your list:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

#### Step 2: add the library

Add the dependencies into the project's `build.gradle`:

```groovy
def arrow_version = "1.0.1"

dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
}
```

If you are using more than one Arrow dependency, you can avoid specifying the same version over and over by using a BOM file:

```groovy
def arrow_version = "1.0.1"

dependencies {
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-core"
    implementation "io.arrow-kt:arrow-optics"
    ...
}
```

#### Step 3: add the plug-in

To get the most of Arrow Optics you can add out Kotlin plug-in to your build, which takes care of generating optics for your data types.

```groovy
plugins {
    id "com.google.devtools.ksp" version "1.6.0-1.0.2"
}

dependencies {
    ksp "io.arrow-kt:arrow-optics-ksp:2.0-SNAPSHOT"
}
```

Now you are ready to learn about the [Optics DSL]({{ '/optics/dsl/' | relative_url }})!

</div>


</div>

</div>


</div>