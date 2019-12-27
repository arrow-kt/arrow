---
layout: docs-fx
title: Fx
permalink: /docs/fx/
---

# Arrow Fx. Typed FP for the masses

Arrow Fx is a next-generation Typed FP Effects Library that makes effectful and polymorphic programming first class in Kotlin, and acts as an extension to the Kotlin native suspend system.

The library brings purity, referential transparency, and direct imperative syntax to typed FP in Kotlin, and is a fun and easy tool for creating Typed Pure Functional Programs.

Arrow Fx programs run unmodified in multiple supported frameworks and runtimes such as Arrow Effects IO, KotlinX Coroutines Deferred, Rx2 Observable, and many others.

# Pure Functions, Side Effects, and Program Execution

## Pure & Referentially Transparent Functions

A pure function is a function that consistently returns the same output when given the same input. Pure functions exhibit a deterministic behavior and cause no observable effects externally. We call this property referential transparency.

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

Performing network or file IO, writing to streams, and, in general, all functions that return `Unit` are very likely to produce side effects. That's because a `Unit` return value denotes *no useful return*, which implies that the function does nothing but perform effects.

In Arrow Fx, we use `suspend fun` to denote a function that may cause side effects when invoked.

In the example below, `println(a : Any): Unit` is a side effect because, every time it's invoked, it causes observable effects by interacting with the `System.out` stream and signals that it produces no useful output by returning `Unit`.

When we denote side effects as `suspend`, the Kotlin compiler will ensure that we're not applying uncontrolled side effects in the pure environment.

```kotlin:ank:fail
//sampleStart
fun helloWorld(): String =
  "Hello World"

suspend fun sayHello(): Unit =
  println(helloWorld())

sayHello()
//sampleEnd  
```

Compiling the snippet above will result in a compilation error.

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

#### `fx` composition

Side effects can be composed and turned into pure values in `fx` blocks.

#### Turning side effects into pure values with `effect`

`effect` wraps the effect and turns it into a pure value by lifting any `suspend () -> A` user-declared side effect into an `IO<Nothing, A>` value.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    val pureHello = effect { sayHello() }
    val pureGoodBye = effect { sayGoodBye() }
  }
//sampleEnd
fun main() {
  println(greet())
}
```

When we capture suspended side effects as `IO` values with `effect`, we can pass them around and compose them until we are ready to apply the effects. At this point, nothing has happened because `greet` and `effect` are lazy values.

#### Applying side effects with `!effect`

We apply side effects with the operator `!`. Once you purify a side effect with `effect`, you can extract its value in a non-blocking way. `!effect` takes the suspended side effects `sayHello()` and `sayGoodbye()` and ensures the continuation context controls them before they get a chance to be executed. This ensures our effect compositions are pure and referentially transparent, and will only run at the edge.

Note that running `greet()` in the previous example does not perform any effects because it returns a wrapped lazy value. Since invoking this function does not produce effects, we can be confident that `greet` is pure and referentially transparent, despite referring to the effects application.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }
//sampleEnd
fun main() {
  println(greet()) //greet is a pure IO program
}
```

An attempt to run a side effect in an `fx` block not delimited by `effect` or `!effect` also results in a compilation error.

```kotlin:ank:fail
import arrow.fx.IO
import arrow.fx.extensions.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    sayHello()
    sayGoodBye()
  }
//sampleEnd
```

Arrow enforces usage to be explicit about effects application.

#### Applying existing datatypes

Composition using regular datatypes such as `IO` is still possible within `fx` blocks just like `effect` blocks. In addition to `!`, you can also use the extension function `bind()` to execute them.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.fx.extensions.fx
//sampleStart
fun sayInIO(s: String): IO<Nothing, Unit> =
  IO { println(s) }

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    sayInIO("Hello World").bind()
  }
//sampleEnd
fun main() {
  println(greet()) //greet is a pure IO program
}
```

## Executing effectful programs

The `greet` program is ready to run as soon as the user is ready to commit to an execution strategy that is either `blocking` or `non-blocking`.
`blocking` execution strategies will block the current thread that's waiting for the program to yield a value, whereas `non-blocking` strategies will immediately return and perform the program's work without blocking the current thread.

Since both blocking and non-blocking execution scenarios perform side effects, we consider running effects as an `unsafe` operation.

Arrow restricts the ability to run programs to extensions of the `UnsafeRun` type class.

Usage of `unsafe` is reserved for the end of the world and may be the only impure execution of a well-typed functional program.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")

suspend fun sayGoodBye(): Unit =
  println("Good bye World!")

fun greet(): IO<Nothing, Unit> =
  IO.fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }

fun main() { // The edge of our world
  unsafe { runBlocking { greet() } }
}
//sampleEnd
```

Arrow Fx emphasizes the guarantee that users understand when they are performing side effects in their program declaration.

Arrow Fx programs are not restricted to `IO` but, in fact, are polymorphic and would work unmodified in many useful runtimes like the ones we find in popular libraries such as KotlinX Coroutines `Deferred`, Rx2 `Observable`, Reactor framework `Flux`, and, in general, any third party data type that can model sync and async effect suspension. See [Issue 1281](https://github.com/arrow-kt/arrow/issues/1281), which tracks support for those frameworks, or reach out to us if you are interested in support for any other framework.

If you're not very familiar with Functional Programming, and you've made it this far, you may realize that, despite the buzzwords and some FP jargon, you already know how to use Arrow Fx in general. This is because Arrow Fx brings the most popular imperative style to effectful programs with few simple primitives for effect control and asynchronous programming.


# Conclusion

Complementing the Kotlin Coroutines library, Arrow Fx adds an extra layer of safety to concurrent and asynchronous programming so you're well aware of where effects are localized in your apps.
It does this by empowering polymorphic programs that can be interpreted untouched, preserving the same declaration in multiple popular frameworks such as Rx2, Reactor, etc.

Arrow Fx offers direct style syntax and effect control without compromises, and removes the syntactic burden of type parametrization while still yielding programs that are pure, safe, and referentially transparent.

Despite some limitations of the Kotlin type system, like lack of Higher Kinded Types, the Kotlin language is excellent for encoding typed programs with a first-class imperative syntax for FP. These programs are approachable by the broader programming community, and arguably easier to encode than some solutions currently used in mainstream Kotlin and Scala FP communities.

Come check out Arrow fx and join the discussion in the Kotlin Slack and Gitter at [arrow-kt.io]()
