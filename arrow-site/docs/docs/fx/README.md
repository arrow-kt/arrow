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
  - [Gradle Setup](#gradle-setup)
  - [Maven Setup](#maven-setup)
- Extensions and data types   
  - [Parallel](parallel/)
  - [Flow](/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/kotlinx.coroutines.flow.-flow/) 
  - [Schedule](/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-schedule/)
  - [Resource](/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-resource/)
  - [CircuitBreaker](/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/)
  - [Atomic](/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/-atomic/)
  - [STM](/apidocs/arrow-fx-stm/arrow.fx.stm/)
- [Integrating with 3rd-party libraries](#integrating-with-3rd-party-libraries)
- Additional Information
  - [Kotlin's Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
  - [Pure & Referentially Transparent Functions](purity-and-referentially-transparent-functions/)
  - [Kotlin's Std Coroutines package](coroutines/)
  - [Why suspend over IO monad](/effects/io/)

## Gradle Setup

```groovy
dependencies {
  implementation "io.arrow-kt:arrow-fx-coroutines:0.13.2"
  implementation "io.arrow-kt:arrow-fx-stm:0.13.2"
}
```

### Snapshot version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this repository:

```groovy
allprojects {
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
}
```

### Gradle BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```groovy
implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

implementation "io.arrow-kt:arrow-fx-coroutines"
implementation "io.arrow-kt:arrow-fx-stm"
```

## Maven Setup

Add the dependencies that you want to use:
```xml
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
    <version>0.13.2</version>
</dependency>
```

### Maven BOM file

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

## Integrating with 3rd-party libraries

Arrow Fx integrates with KotlinX Coroutines Fx, Reactor framework, and any library that can model effectful async/concurrent computations as `suspend`.

If you are interested in the Arrow Fx library, please contact us in the #Arrow channel on the official [Kotlin Lang Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) with any questions and we'll help you along the way.
