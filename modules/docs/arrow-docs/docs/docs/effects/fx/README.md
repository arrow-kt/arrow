---
layout: docs
title: Fx
permalink: /docs/effects/fx/
---

- [Arrow Fx. Typed FP for the masses](#arrow-fx-typed-fp-for-the-masses)
  * [Pure Functions, Side Effects and Program Execution](#pure-functions-side-effects-and-program-execution)
    + [Pure & Referentially Transparent Functions](#pure--referentially-transparent-functions)
    + [Side effects](#side-effects)
        * [`suspend` composition](#suspend-composition)
        * [`fx` composition](#fx-composition)
        * [Turning side effects into pure values with `effect`](#turning-side-effects-into-pure-values-with-effect)
        * [Applying side effects with `!effect`](#applying-side-effects-with-effect)
    + [Executing effectful programs](#executing-effectful-programs)
- [Conclusion](#conclusion)

# Arrow Fx. Typed FP for the masses

Arrow Fx brings purity, referential transparency and direct imperative syntax to typed FP in Kotlin.

Arrow Fx programs run unmodified in multiple supported frameworks and runtimes such as Arrow Effects IO, KotlinX Coroutines Deferred, Rx2 Observable and many more.

Creating Typed Pure Functional Programs is fun and easy with Arrow Fx.
 
# Pure Functions, Side Effects and Program Execution

## Pure & Referentially Transparent Functions

A pure function is a function that returns the same output consistently given the same input. 
Pure functions exhibit a deterministic behavior and cause no external observable effects.

We call this property referential transparency.

Referential transparency allow us to reason about the different pieces of our program in isolation. 

To create a pure function in Kotlin we may use the keyword `fun`.

```kotlin:ank:playground
//sampleStart
fun helloWorld(): String =
  "Hello World"
//sampleEnd  
fun main() {
  println(helloWorld())
}
```

We can state that `helloWorld` is a pure and referentially transparent function because invoking `helloWorld()` consistently returns the same output given the same input and does not produce observable changes in the external world.

## Side effects

A side effect is an external observable effect a function performs beside returning a value.

Performing network or file IO, writing to streams and in general all functions that return `Unit` are very likely to produce side effect. That is because a `Unit` return value denotes *no useful return* which implies the function does nothing but perform effects.

In Arrow Fx we use `suspend fun` to denote a function that when invoked may cause side effects.

In the example below `println(a : Any): Unit` is a side effect because each time it's invoked it causes observable effects by interacting with the `System.out` stream and it signals that it produces no useful output by returning `Unit`

When we denote side effects as `suspend` the Kotlin compiler will ensure we are not applying side effects uncontrolled in the pure environment.

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

The Kotlin compiler disallows the invocation of suspended functions in the pure environment because a `suspend fun` requires either to be declared inside another suspended function or a continuation.

A continuation is a Kotlin Interface `Continuation<A>` that proves we know how to handle success and error cases resulting from running the suspended effect.

This is a great built in feature of the Kotlin compiler that already makes it an ideal choice for Typed FP but not the only one. 

Continue reading on if you are curious to see how the Kotlin Compiler and Arrow Fx can get rid of a lot of the functional idioms by using direct syntax over all effectful monads.

#### `suspend` composition

Applying and composing suspended side effects it's allowed in the presence of other suspended side effect. 
In the example below `sayHello` and `sayGoodBye` are valid inside `greet` because all of them are suspended functions.

```kotlin:ank:playground
//sampleStart
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
suspend fun sayHello(): Unit =
  println(helloWorld())
  
suspend fun greet(): Unit {
  sayHello() // this is ok because
  sayGoodBye() // `greet` is also `suspend`
}
//sampleEnd  
```

#### `fx` composition

Side effects can be composed and turned into pure values in `fx` blocks. 

#### Turning side effects into pure values with `effect` 

`effect` wraps the effect and turns it into a pure value by lifting any `suspend () -> A` user declared side effect into a `IO<A>` value

```kotlin:ank:playground
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")
  
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
fun greet(): IO<Unit> =
  fx {
    val pureHello = effect { sayHello() }
    val pureGoodBye = effect { sayHello() }
  }
//sampleEnd 
fun main() {
  println(greet())
}
```

When we capture suspended side effects as `IO` values with `effect`, we can pass them around and compose them until we are ready to apply the effects. At this point nothing has happened yet because `greet` and `effect` are lazy values.

#### Applying side effects with `!effect`

Side effects are applied with the operator `!`. Once you purify a side effect with `effect` you can extract it's value in a non blocking way. `!effect` takes the suspended side effects `sayHello()` and `sayGoodbye()` and ensures they are controlled by the continuation context before they get a chance to be executed. This ensures our effect compositions are pure and referentially transparent and will only run at the edge.
Note running `greet()` in the previous example does not perform any effects because it returns a wrapped lazy value.
Since invoking this function does not produce effects we can be confident that `greet` is pure and referentially transparent despite referring to effects application.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")
  
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
fun greet(): IO<Unit> =
  fx {
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
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")
  
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
fun greet(): IO<Unit> =
  fx {
    sayHello()
    sayGoodBye()
  }
//sampleEnd 
```

Arrow enforces usage to be explicit about effects application.

## Executing effectful programs

The `greet` program is ready to run as soon as the user is ready to commit to an execution strategy that is either `blocking` or `non-blocking`.
`blocking` execution strategies will block the current thread waiting for the program to yield a value whereas `non-blocking` strategies will immediately return and perform the program's work without blocking the current thread.

Since both blocking and non-blocking execution scenarios perform side effects we consider running effects an `unsafe` operation. 

Arrow restricts the ability to run programs to extensions of the `UnsafeRun` type class. 

Usage of `unsafe` is reserved to the end of the world and may be the only impure execution of a well typed functional program.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")
  
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
fun greet(): IO<Unit> =
  fx {
    !effect { sayHello() }
    !effect { sayGoodBye() }
  }

fun main() { // The edge of our world
  unsafe { runBlocking { greet() } }
}
//sampleEnd 
```

Arrow Fx makes emphasis in guaranteeing users understand when they are performing side effects in their program declaration.

Arrow Fx programs are not restricted to `IO` but in fact polymorphic and would work unmodified in many useful runtimes like the ones we find in popular libraries such as KotlinX Coroutines `Deferred`, Rx2 `Observable`, Reactor framework `Flux` and in general any third party data type that can model sync and async effect suspension. See [Issue 1281](https://github.com/arrow-kt/arrow/issues/1281) which tracks support for those frameworks or reach out to us if you are interested in support for any other framework.

If you've come this far and you are not too familiar with FP you may have realized that despite the buzzwords and some FP jargon, you already know how to use Arrow Fx for the most part. That is because Arrow Fx brings the most popular imperative style to effectful programs with few simple primitives for effect control and asynchronous programming. 


# Conclusion

Arrow Fx is a next generation Typed FP Effects Library that makes effectful and polymorphic programing first class in Kotlin acting as an extension to the Kotlin native suspend system.
Complementing the Kotlin Coroutines library, Arrow Fx adds an extra layer of safety to concurrent and asynchronous programming making you well aware were effects are localized in your apps.
It does all this by empowering polymorphic programs that can be interpreted untouched preserving the same declaration in multiple popular frameworks such as Rx2, Reactor, etc.

Arrow Fx offers direct style syntax and effect control without compromises and removes the syntactic burden of type parametrization while still yielding programs that are pure, safe and referentially transparent.

Despite some of the Kotlin type system limitations like lack of Higher Kinded Types, The Kotlin language is excellent to encode typed programs with first class imperative syntax for FP. These programs are approachable by the broader programming community and arguably easier to encode than some solutions currently used in mainstream Kotlin and Scala FP communities.

Come check out Arrow fx and join the discussion in the Kotlin Slack and Gitter at [arrow-kt.io]()
