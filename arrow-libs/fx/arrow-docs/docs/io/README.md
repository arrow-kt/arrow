---
layout: docs-fx
title: IO
permalink: /effects/io/
---

## IO

IO is being deprecated in Arrow in favor of Arrow Fx Coroutines which transparently integrates with Kotlin suspend functions and the KotlinX Coroutines library.

Arrow has adopted suspend as system to model monadic computations and offers the same api and additional features as top level extensions functions over `suspend () -> A` whereas before it was `IO<A>`.
Some functions like flatMap are now replaced by simple function invocation.

For an overview of the functions offered by Arrow Fx Coroutines visit: [https://arrow-kt.io/docs/fx/async/](https://arrow-kt.io/docs/fx/async/)
