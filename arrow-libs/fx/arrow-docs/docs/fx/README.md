---
layout: docs-fx
title: Fx
permalink: /fx/
---

# Arrow Fx. Typed FP for the masses

Arrow Fx is a next-generation Typed FP Effects Library that makes tracked effectful programming first class in Kotlin,
and is build on top of Kotlin's suspend system.

The library brings purity, referential transparency, and direct imperative syntax to typed FP in Kotlin, and is a fun and easy tool for creating Typed Pure Functional Programs.

# Pure Functions, Side Effects, and Program Execution

## Pure & Referentially Transparent Functions

A pure function is a function that consistently returns the same output when given the same input.
Pure functions exhibit a deterministic behavior and cause no observable effects externally. We call this property referential transparency.

Referential transparency allows us to reason about the different pieces of our program in isolation.

To create a pure function in Kotlin, let's use the keyword `fun`:

```kotlin:ank:playground
//sampleStart
fun helloWorld(): String =
  "Hello World"

//sampleEnd  
fun main() {
  println(helloWorld())
}
```

We can state that `helloWorld` is a pure and referentially transparent function because invoking `helloWorld()` consistently returns the same output given the same input, and does not produce observable changes in the external world.

## Side effects

A side effect is an externally observable effect a function performs in addition to returning a value.

Performing network or file I/O, writing to streams, and, in general, all functions that return `Unit` are very likely to produce side effects. That's because a `Unit` return value denotes *no useful return*, which implies that the function does nothing but perform effects.

In Arrow Fx, we use `suspend fun` to denote a function that may cause side effects when invoked.

In the example below, `println(a : Any): Unit` is a side effect because, every time it's invoked, it causes observable effects by interacting with the `System.out` stream and signals that it produces no useful output by returning `Unit`.

When we denote side effects as `suspend`, the Kotlin compiler will ensure that we're not applying uncontrolled side effects in the pure environment.

```kotlin
//sampleStart
fun helloWorld(): String =
  "Hello World"

suspend fun sayHello(): Unit =
  println(helloWorld())

fun main() {
  sayHello()
}
//sampleEnd  
```

Compiling the snippet above will result in `javax.script.ScriptException: error: suspend function 'sayHello' should be called only from a coroutine or another suspend function` compilation error for `sayHello` call.

The Kotlin compiler disallows the invocation of suspended functions in the pure environment because `suspend fun` requires declaration inside another suspended function or a continuation.

A continuation is a Kotlin Interface, `Continuation<A>`, that proves we know how to handle success and error cases resulting from running the suspended effect.

This is a great built-in feature of the Kotlin compiler that already makes it an ideal choice for Typed FP, but it's not the only one.

Continue reading on if you're curious to see how the Kotlin Compiler and Arrow Fx can eliminate many of the functional idioms by using direct syntax, overall effectful monads.

#### `suspend` composition

Applying and composing suspended side effects is allowed in the presence of other suspended side effects.

In the example below, `sayHello` and `sayGoodBye` are valid inside `greet` because they are all suspended functions.

```kotlin
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun greet(): Unit {
  sayHello() // this is ok because
  sayGoodBye() // `greet` is also `suspend`
}
```

## Executing effectful programs

The `greet` program is ready to run as soon as the user is ready to commit to an execution strategy that is either `blocking` or `non-blocking`.
`blocking` execution strategies will block the current thread that's waiting for the program to yield a value, whereas `non-blocking` strategies will immediately return and perform the program's work without blocking the current thread.
Since both blocking and non-blocking execution scenarios perform side effects, we consider running effects as an `unsafe` operation.

Arrow offers an `Enviroment` type class to run a suspended program.
Usage of unsafe runner functions (like `unsafeRunSync` in this case) is reserved for the end of the world and may be the only impure execution of a well-typed functional program.

```kotlin:ank:playground
import arrow.fx.coroutines.*

//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

suspend fun greet(): Unit {
  sayHello()
  sayGoodBye()
}

fun main() { // The edge of our world
  val env = Environment()
  env.unsafeRunSync { greet() }
}
//sampleEnd
```

In cases where you can use `suspend` edge-points, you should always prefer to do so. I.e. Kotlin also offers a `suspend fun main`.

```kotlin:ank:playground
import arrow.fx.coroutines.*

suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

suspend fun greet(): Unit {
  sayHello()
  sayGoodBye()
}

//sampleStart
suspend fun main(): Unit = // The edge of our world
  greet()
//sampleEnd
```


Arrow Fx emphasizes the guarantee that users understand when they are performing side effects in their program declaration.

If you're not very familiar with Functional Programming, and you've made it this far, you may realize that, despite the buzzwords and some FP jargon, you already know how to use Arrow Fx in general.
This is because Arrow Fx brings the most popular imperative style to effectful programs with few simple primitives for effect control and asynchronous programming.

# Conclusion

Arrow Fx offers an idiomatic way of doing effecfull FP with Kotlin's coroutine system.
It does this by providing support for cancellation, error handling, resource handling and all goodies you might be familiar with from other functional effect systems.

Arrow Fx offers direct style syntax and effect control without compromises, and removes the syntactic burden of type parametrization while still yielding programs that are pure, safe, and referentially transparent.

Despite some limitations of the Kotlin type system, like lack of Higher Kinded Types, the Kotlin language is excellent for encoding typed programs with a first-class imperative syntax for FP.
These programs are approachable by the broader programming community, and arguably easier to encode than some solutions currently used in mainstream Kotlin and Scala FP communities.

Come check out Arrow fx and join the discussion in the [Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) and [Gitter](https://gitter.im/arrow-kt/Lobby)
