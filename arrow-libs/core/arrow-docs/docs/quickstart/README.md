---
layout: docs-core
title: Quick Start
permalink: /core/
---

[![Maven Central](https://img.shields.io/maven-central/v/io.arrow-kt/arrow-core?color=%234caf50)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
<!-- Remove the following on when having just one WIP again -->
[![Latest snapshot](https://img.shields.io/badge/latest%20snapshot-v0.10.5--SNAPSHOT-blue)](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/arrow-core/)
[![Latest snapshot](https://img.shields.io/maven-metadata/v?label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.jfrog.org%2Fartifactory%2Foss-snapshot-local%2Fio%2Farrow-kt%2Farrow-core%2Fmaven-metadata.xml)](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/arrow-core/)
[![Release Status](https://github.com/arrow-kt/arrow/workflows/Release/badge.svg)](https://github.com/arrow-kt/arrow/actions?query=workflow%3ARelease+branch%3Amaster)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-black.svg?logo=stackoverflow)](https://stackoverflow.com/questions/tagged/arrow-kt)

Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow aims to provide a [*lingua franca*](https://en.wikipedia.org/wiki/Lingua_franca) of interfaces and abstractions across Kotlin libraries.
For this, it includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` to empower users to write pure FP apps and libraries built atop higher order abstractions.

Use the list below to learn more about Λrrow's main features.

- [Patterns]({{ '/patterns/glossary/' | relative_url }}): tutorials and approaches to day-to-day challenges using FP
- [Libraries]({{ '/quickstart/libraries/' | relative_url }}): all the libraries provided by Λrrow
- [Type classes]({{ '/typeclasses/intro/' | relative_url }}): defining behaviors for data
- [Data types]({{ '/datatypes/intro/' | relative_url }}): common abstractions
- [Effects]({{ '/effects/io/' | relative_url }}): interfacing with external systems
- [Optics]({{ '/optics/iso/' | relative_url }}): inspecting and modifying data structures

## Curated external links

- [Projects and Examples]({{ '/quickstart/projects/' | relative_url }})
- [Media](https://media.arrow-kt.io)

## Setup

Take a look at [Setup]({{ '/quickstart/setup/' | relative_url }}) where you'll find the instructions to configure a project with `Gradle` or `Maven` to use `Arrow` library.
