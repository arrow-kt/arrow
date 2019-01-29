---
layout: docs
title: Fx
permalink: /docs/effects/fx/
---

# Arrow Fx. Typed FP for the masses

Creating Typed Pure Functional Programs is fun and easy with Arrow Fx.

Arrow Fx brings purity, referential transparency and direct imperative syntax to effectful programs declared as suspended functions.

Arrow Fx programs can run unmodified in multiple supported frameworks and runtimes such as Arrow Effects IO, KotlinX Coroutines Deferred, Rx2 Observable and many more.

The program below illustrates the *Hello World* of effectful programming with Arrow Fx:
 
## Introduction

### Pure functions

A pure function is a function that returns the same output consistently given the same input.
to create a pure function in Kotlin we may use keyword `fun`.

```kotlin:ank:playground
//sampleStart
fun helloWorld(): String =
  "Hello World"
//sampleEnd  
fun main() {
  println(helloWorld())
}
```

We can state that `helloWorld` is a pure function because invoking `helloWorld()` consistently returns the same output given the same input and does not produce observable changes in the external world.

### Side effects

A side effect is an observable effect a function performs beside returning a value.

We use `suspend fun` to denote a functions that may cause side effects.

In the example below `println` is a side effect because each time it's invoked it causes observable effects by interacting with the `System.out` stream

When we denote side effects as `suspend` the Kotlin compiler will ensure we are not applying side effects uncontrolled in the pure environment.

```kotlin:ank:playground
//sampleStart
suspend fun sayHello(): Unit =
  println(helloWorld)

sayHello()
//sampleEnd  
```

Running the snippet above will show how compilation fails.

The Kotlin compiler disallows a suspend function in a pure environment because a `suspend fun` requires a continuation.
A continuation proves we know how to handle success and error cases resulting from running the effect.

This is a great built in feature of the Kotlin compiler that we can use to denote side effects and make them pure by definition due to their inhability
to compile/run unless they are in the pressence of a continuation that can handle the result of applying the side effects.

Arrow implements all the continuations and suspension system needed to safely compose and run side effects in a pure functional program.

### Composing Side Effects

#### `suspend` composition

Applying and composing suspended side effects it's allowed in the presence of other suspended side effect. 
In the example below `sayHello` and `sayGoodBye` are valid inside `greet` because all of them are suspended functions.

```kotlin:ank:playground
//sampleStart
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
suspend fun sayHello(): Unit =
  println(helloWorld)
  
suspend fun greet(): Unit {
  sayHello() // this is ok because
  sayGoodBye() // `greet` is also `suspend`
}
//sampleEnd  
```

#### `fx` composition

Side effects can be composed and turned into pure values in `fx` blocks. 

#### Applying side effects with `effect`

```kotlin:ank:playground
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
  greet().unsafeRunSync()
}
```

Side effects are applied with the function `!effect`. `!effect` takes the suspended side effects `sayHello()` and `sayGoodbye()` and ensures they are controlled by the `IO` context before they get a chance to be executed. This ensures our effect compositions are pure and referentially transparent and will only run as part of an `IO` program.

An attempt to run a side effect in an `fx` block not delimited by `!effect` also results in a compilation error. 

```kotlin:ank:playground
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

Arrow enforces usage to be explicit about side effect application.

You may have noticed we don't use `suspend fun` for `greet` in this case. 
This is because `greet`'s return type is `IO` which is already a pure value and as such does not need to be suspended.

#### Turning side effects into pure values with `effect` 

While we use `!effect` to bind side effect the same keyword without `!` wraps the effect and turns it into a pure value in the context of `F<A>` where `F` in this case is `IO`. 
This value may be manually destructured, pass around and composed in general with other `IO` values. 
Unlike `!effect` which immediately binds the effect `effect` preserves the value in it's wrapped form.

```kotlin:ank:playground
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun sayHello(): Unit =
  println("Hello World")
  
suspend fun sayGoodBye(): Unit =
  println("Good bye World!")
  
fun greet(): IO<Unit> =
  fx {
    val pureHello: IO<Unit> = effect { sayHello() }
    val puregoodBye: IO<Unit> = effect { sayHello() }
    val (_ : Unit) = pureHello
    val (_ : Unit) = pureGoodBye
  }
//sampleEnd 
fun main() {
  greet().unsafeRunSync()
}
```

When we capture suspended side effects as `IO` values with `effect`, we can pass them around and compose them until we are ready to apply the effects.
We can extract the pure value's content and use their binding to continue composing our program.

Note that while we are talking about effect application we are not running anything just yet.
All `fx` blocks over effectful capable data types like `IO` will always yield a non-executed program that we can explicitly run once we are ready to.

Arrow Fx programs are not restricted to `IO` but in fact polymorphic and would work unmodified in many useful runtimes like the ones we find in popular libraries such as KotlinX Coroutines `Deferred`, Rx2 `Observable`, Reactor framework `Flux` and in general any third party data type that can model sync and async effect suspension.

### Executing effectful programs

The `greet` program is ready to run as soon as the user is ready to commit to an execution strategy that is either `blocking` or `non-blocking`.
`blocking` execution strategies will block the current thread waiting for the program to yield a value whereas `non-blocking` strategies will immediately return and perform the program's work without blocking the current thread.

Since both blocking and non-blocking execution scenarios perform side effects we consider running effects an `unsafe` operation. 

Arrow restricts the ability to run programs to extensions of the `UnsafeRun` type class. 

Usage of `unsafe` is reserved to the end of the world and may be the only impure execution of a well typed functional program.

```kotlin:ank:playground
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
  unsafe { runBlocking { program } }
}
//sampleEnd 
```

Arrow Fx makes emphasis in guaranteeing users understand when they are performing side effects in their program declaration.

If you've come this far and you are not too familiar with FP you are probably realizing by now that you already know how to use Arrow Fx
for the most part. That is because Arrow Fx brings the most popular imperative style to effectful programs. A style that most OOP developers coming from other backgrounds are already accustomed to. 

# Arrow Fx. Typed FP for the masses.

## Introduction
### Pure functions
### Side Effects
#### Composing `suspend` functions
#### Pure `fx` compositional blocks
#### Controlling side effect application with `effect`
#### Turning side effects into pure values with `f`

## Asynchronous & Concurrent Programming
### Switching Dispatchers and Contexts
### Fibers
### Parallelization & Concurrency
### Arrow Fx vs KotlinX Coroutines
### Integrating with third party libraries

## Polymorphism. One Program multiple runtimes
### Creating polymorphic programs
### Fx for all data types
### Arrow Fx vs Tagless Final
### Assimilation of Functor, Applicative & Monad
### Auto binding and commutative monads

## Conclusion

`Fx` eliminates the need for tagless style algebras and parametrization over expressions that require `F` as a type parameter.
 
`Fx` models effects as `suspend` functions that are disallowed to compile in the pure environment but welcome in compositional blocks denoted as `effect { sideEffect() }`.

 `fx` brings first class, no-compromises direct style syntax for effectful programs.
 It models effects with the Kotlin Compiler native system support for `suspend` functions and abilities to declare restricted suspended blocks with `@RestrictsSuspension` in which effects are allowed to run and compose.

 As a consequence of the assimilation and elimination of the `F` type parameter from the syntax in such programs the entire hierarchy of type class combinators we find in `Functor`, `Applicative` and `Monad` dissapear and what they model is swallowed as syntax that operates directly over the environment.

 If you have been doing Tagless Final style programs or in general polymorphic programs where side effects were explicitly controlled in a C like lang like Java, Scala or Kotlin you are going to be interested in what the examples below demonstrate.
 Fx opens the syntax making it approachable to both FP and OOP programers that are unfamiliar with type parametrization and explicit monadic design in general.

This simplification is manifested in the world of suspended effects in the fact that all values of type `Kind<F, A>` can bind to `A` in the left hand side in a non blocking fashion because Kotlin supports imperative CPS and continuation styles syntactically.

This has a tremendous impact in syntax since all the functional combinators where before you had as return type a `Kind<F, A>`
are gone. map, flatMap, traverse, sequence now are either gone from the syntax or their returned values are flattened and automatically
bound in the monad context and transparent to the user.

This leads us to realize that there is a direct relationship between `suspend () -> A` and `Kind<F, A>` or what in Scala is `F[A]`.
This relationship establishes that a `suspend` function denoting an effect can be lifted to operate in the monadic context of a
suspend capable data type.

This relationship is also true for polymorphic context and encodings. For all data types that provides extensions for `Async<F>` we can define:

```kotlin
fun <A> (suspend () -> A).effect(): Kind<F, A> =
    async { cb ->
      startCoroutine(object : Continuation<A> {
        override fun resume(value: A) {
          cb(value.right())
        }
        override fun resumeWithException(exception: Throwable) {
          cb(exception.left())
        }
        override val context: CoroutineContext = EmptyCoroutineContext
      })
    }
```

`effect()` takes us without blocking semantics from a suspended function to any `Kind<F, A>` for which an `Async<F>` extension exists.
This includes IO and pretty much everything you are using today in a tagless final or IO wrapping style in FP.

This relationship also eliminates the need to ever use any of the functional combinators you find in the `Functor<F>`
hierarchy. All of them are swallowed by equivalent direct syntax in the environment as demonstrated in the examples below:

## Removing the need for the `Functor`, `Applicative` and `Monad` combinators

`Arrow Fx` removes the need to use the functional combinators found in the Functor, Applicative and Monad type classes.
These combinators are instead represented as direct syntax and compile-time guaranteed by the Kotlin compiler that effectful computations denoted by the user can't run uncontrolled in functions denoted as pure.

The following combinators illustrate how the Functor hierarchy functions are pointless in the environment given we can declare programs that respect the same semantics thanks to the Kotlin suspension system. These below are examples of a few of the most well known combinators that would disappear from your day to day FP programming and what others would look like flattened in the environment
via automatic suspended binding after each combinator is applied in the suspended environment:

| TypeClass           | Signature | Arrow Fx signature |
|---------------------|-----------|--------------------|
| Functor.map         | `just(1).map { it + 1 }` | `1 + 1`  |
| Applicative.just    | `just(1)` | `1` |
| Applicative.mapN    | `mapN(just(1), just(2), ::Tuple2)` | `1 toT 2` |
| Applicative.tupled  | `tupled(just(1), just(2))` | `1 toT 2` |
| Monad.flatMap       | `IO { 1 }.flatMap { n -> IO { n + 1 } }` | `1 + 1` |
| Monad.flatten       | `IO { IO { 1 } }.flatten()` | `1` |
| MonadDefer.delay    | `IO.delay { 1 }` | `effect { 1 }` |
| MonadDefer.defer    | `IO.defer { IO { 1 } }` | `effect { 1 }` |

## Computing over other non-suspended monads 

`Continuation<A>` is declared in Kotlin as this interface which makes no assumptions whether resumption of the program
happens in a blocking or non-blocking style.

The Arrow library already provides the ability to compute imperatively over all monads and brings first class do notation / comprehensions to all monads with an extremely elegant syntax thanks to Kotlin's operator overloading features and suspension system.

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.extensions.option.fx.fx

//sampleStart
val result = fx {
  val (one) = Option(1)
  val (two) = Option(one + one)
  two
}
//sampleEnd

fun main() {
  println(result)
}
```

```kotlin:ank:playground
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx

//sampleStart
val result = fx {
  val (one) = IO { 1 }
  val (two) = IO { one + one }
  two
}
//sampleEnd

fun main() {
  println(result)
}
```

The `component1` operator over all `Kind<F, A>` is able to obtain a non blocking `A` bound in the LHS. 
The result is destructuring syntax for all monadic expressions by receiving the value bound in the left hand side in a non blocking fashion.

```kotlin
suspend fun <A> Kind<F, A>.bind(): A
suspend operator fun <A> Kind<F, A>.component1(): A = bind()
```

### Enhancing the ergonomics of Concurrent and Async FP Programs

By eliminating this nesting over `Kind<F, A` or `F[A]` and representing suspended computations as `suspend () -> A` Arrow Fx eliminates
also all nested return types from classical type class combinators in the effect hierarchy.

The examples below demonstrates the expressive power that suspended functions and Arrow Fx brings to reactive programming by eliminating
effects nesting in a simple program that uses parallelism to process effectful computations in parallel and non-blocking.

#### Concurrent parMap

```kotlin:ank:playground
import arrow.effects.extensions.io.fx.fx
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import kotlinx.coroutines.Dispatchers

/** A user declared side effect **/
//sampleStart
suspend fun getThreadName(): String =
  Thread.currentThread().name

val program = fx {
  // note how the receiving value is typed in the environment and not inside IO despite being effectful and
  // non-blocking parallel computations
  val result: List<String> = 
    Dispatchers.Default.parMap(
      { getThreadName() }, // each one of these runs in parallel
      { getThreadName() }
    ) { a, b -> 
      listOf(a, b) 
    }
}
//sampleEnd
fun main() {
  unsafe { runBlocking { program } }
}
```

The program above shows how `Concurrent.parMap` frequently expressed with a type signature that includes a wrapped
returned value is unwrapped and returned to the environment while preserving the same semantics of an IO based program.
A simplification to represent parMap similar to how Cats Effect, Arrow Effects and Arrow fx with its suspended DSL represents
functional combinators is shown below:

Kotlin Kinded
```kotlin
fun <F, A, B, C> Concurrent<F>.parMap(fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C>
```

Scala Kinded
```scala
def parMap[F[_], A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C)(implicit F: Concurrent[F]): F[C]
```

Kotlin Suspended
```kotlin
suspend fun <A, B, C> Concurrent<F>.parMap(fa: suspend () -> A, fb: suspend () -> B, f: (A, B) -> C): C
```

We can observe how both the Scala and Kotlin kinded representations return a value wrapped in `F` whereas the Arrow Fx Kotlin Suspended version returns just `C`. This is because `Arrow Fx` automatically binds effects in the monadic environment and it eliminates the need to refer to `F` when composing effectful programs.

You may have heard before as joke in the interwebs that the answer to most FP problems is `traverse`.
`traverse` is also a victim of the Arrow Fx and Kotlin suspended environment environment :

Kotlin Kinded
```kotlin
fun <F, G, A, B> Traverse<F>.listTraverse(fga: List<Kind<F, A>>, f: (A) -> B): Kind<F, List<B>>
```

Scala Kinded
```scala
def listTraverse[F[_]: Traverse, A, B](ffa: List[F[A]])(f: A => B): F[List[B]]
```

Kotlin Suspended
```kotlin
suspend fun <A, B> List<suspend () -> A>.listTraverse(f: (A) -> B): B
```

The simplification fo the Arrow Fx Kotlin suspended form makes us realize that traversing over a list of effects
is trivial if your environment understand what a suspended effect is and has direct syntax:

```kotlin
suspend fun <A, B> List<suspend () -> A>.traverse(f: (A) -> B): B =
  map { !effect { f(it()) } }
```

# Conclusion

The Arrow Fx system offers direct style syntax and effect control without compromises and removes the syntactic burden of type parametrization while still yielding polymorphic programs that are pure, safe and referentially transparent.

Despite some the Kotlin type system limitations like lack of Higher Kinded Types, The Kotlin language is more than capable of producing
purely typed programs with first class imperative syntax for FP that is approachable by the broader programming community.

Come check out Arrow fx and join the discussion in the Kotlin Slack and Gitter at [arrow-kt.io]()
