---
layout: docs-fx
title: "Kotlin Standard Library and Arrow Fx Coroutines"
permalink: /fx/coroutines/
---

# Kotlin Standard Library & Arrow Fx Coroutines

## Demystify Coroutine 

Kotlin's standard library defines a `Coroutine` as an instance of a suspendable computation.

In other words, a `Coroutine` is a compiled `suspend () -> A` program wired to a `Continuation`.

Which can be created by using [`kotlin.coroutines.intrinsics.createCoroutineUnintercepted`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.intrinsics/create-coroutine-unintercepted.html).

So let's take a quick look at an example.

```kotlin:ank
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

suspend fun one(): Int = 1

val cont: Continuation<Unit> = ::one
  .createCoroutineUnintercepted(Continuation(EmptyCoroutineContext, ::println))

cont.resume(Unit)
```

As you can see here above we create a `Coroutine` using `createCoroutineUnintercepted` which returns us `Continuation<Unit>`.
Strange, you might've expected a `Coroutine` type but a `Coroutine` in the type system is represented by `Continuation<Unit>`.

This `typealias Coroutine = Contination<Unit>` will start running every time you call `resume(Unit)`, which allows you to run the suspend program as many times as you want.

## Kotlin Standard Library Coroutines

[Kotlin Std Coroutines](img/kotlin-stdlib.png)

The standard library offers a powerful set of primitives to build powerful applications on top of `Continuation`s,
together with the compiler's ability to rewrite continuation based code to a beautiful `suspend` syntax.

They can be used to implement a very wide range use-cases, and or *not* bound to asynchronous -or concurrency use-cases.

- Arrow Core, offers computational DSLs build on top of Kotlin's Coroutines `either { }`, `validated { }`, etc
- [`DeepRecursiveFunction`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-deep-recursive-function/) explained [here](https://medium.com/@elizarov/deep-recursion-with-coroutines-7c53e15993e3)
- Another well-known async/concurrency implementation beside Arrow Fx Coroutines is [KotlinX Coroutines](https://github.com/Kotlin/kotlinx.coroutines).

The above image is not exhaustive list of the primitives you can find in the standard library.
For an exhaustive list check the Kotlin Standard Library API docs:
 - [`kotlin.coroutines`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/)
 - [`kotlin.coroutines.intrinsics`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.intrinsics/)
