---
layout: docs-core
title: Glossary
permalink: /patterns/glossary/
---

## Functional Programming Glossary




Note: This section keeps on growing! Keep an eye on it from time to time.

This document is meant to be an introduction to Functional Programming for people from all backgrounds.
We'll go through some of the key concepts, and then dive into their implementation in real world cases.

Some similar documents focused on explaining general concepts, rather than Arrow's versions,
can be found for examples [in JavaScript](https://github.com/hemanth/functional-programming-jargon) and [in Scala](https://gist.github.com/jdegoes/97459c0045f373f4eaf126998d8f65dc).

### Datatypes

A datatype is a class that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalized for all possible uses.

Some common patterns expressed as datatypes are absence handling with [`Option`]({{ '/apidocs/arrow-core/arrow.core/-option/' | relative_url }}),
branching in code with [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }}),
or interacting with the platform the program runs in using `suspend`.

Some of these patterns are implemented using a mix of `sealed` classes, where each inheritor is a `data` class.
For example, the internal representation of an `Option` is a `sealed` class with two `data` classes: `Some<A>(val a: A)`, and `None`.
And `Ior` is a `sealed` class with three `data` class inheritors: `Left(val a: A)`, `Right(val b: B)`, and `Both(val a: A, val b: B)`.

### Side-effects and Effects

A side-effect is a statement that changes something in the running environment. Generally, this means setting a variable, displaying a value on screen, writing to a file or a database, logging, start a new thread . . .

When talking about side-effects, we generally see functions that have the signature `(...) -> Unit`, meaning that, unless the function doesn't do anything, there's at least one side-effect. Side-effects can also happen in the middle of another function, which is an undesirable behavior in Functional Programming.

Side-effects are too general to be unit tested for because they depend on the environment. They also have poor composability. Overall, they're considered to be outside the Functional Programming paradigm, and are often referred to as "impure" functions.

Because side-effects are unavoidable in any program, FP provides several datatypes for dealing with them! One way is by abstracting their behavior. The simplest examples of this are the `Writer`datatype, which allows you to write to an information sink like a log or a file buffer; or `State` datatype, which simulates scoped mutable state for the duration of an operation.

For more complicated effects that can throw or jump threads, we need more advanced techniques. We model side-effects in kotlin with `suspend` functions and Kotlin Continuations. These continuations compose, catch exceptions, control asynchrony, and, most importantly, can be run lazily. This gets rid of the issues with side-effects.

Although one can also write the whole program in an imperative way inside a single Effect wrapper, that wouldn't be very efficient, as you don't get any of its benefits. :D
