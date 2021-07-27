---
layout: docs-fx
title: Arrow Fx. Typed FP for the masses
permalink: /fx/
---

# Arrow Fx. Typed FP for the masses

Arrow Fx is a next-generation Typed FP Effects Library that makes tracked effectful programming first class in Kotlin built on top of Kotlin's suspend system and [KotlinX Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html)
Arrow Fx is a functional companion to KotlinX Coroutines augmenting its api with well known functional operators making it easier to compose async and concurrent programs.

The library brings purity, referential transparency, and direct imperative syntax to typed FP in Kotlin, and is a fun and easy tool for creating Typed Pure Functional Programs.

If you're not familiar yet with Coroutines in Kotlin, it's recommended to first read Kotlin's [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) on KotlinX Coroutines.

## Arrow Fx Coroutines Overview 

 - Getting Started
    - [Kotlin's Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
    - [Pure & Referentially Transparent Functions](...)
    - [Coroutines](..) 
 - [Parallel operators](race / parZip / parTraverse & co)
 - [Schedule](link to Schedule)
 - [Resource handling](link to Resource / small reference to bracket from Resource docs)
 - [Flow Extensions](link to types)
 - [CircuitBreaker](link to CircuitBreaker)
 - [Atomic](link to Atomic)
 - [STM](link to STM)

## Depending on Arrow Fx libraries

### Gradle

```groovy
dependencies {
  implementation "io.arrow-kt:arrow-fx-coroutines:0.13.2"
  implementation "io.arrow-kt:arrow-fx-stm:0.13.2"
}
```

#### Snapshot version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this repository:

```groovy
allprojects {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

implementation "io.arrow-kt:arrow-fx-coroutines"
implementation "io.arrow-kt:arrow-fx-stm"
```

### Maven

#### Basic Setup

Add the dependencies that you want to use:
```xml
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
    <version>0.13.2</version>
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
</dependencyManagement>
```
