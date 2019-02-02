---
layout: docs
title: Fx
permalink: /docs/effects/fx/
---

# Arrow Fx. Typed FP for the masses

Arrow Fx brings purity, referential transparency and direct imperative syntax to typed FP in Kotlin.

Arrow Fx programs run unmodified in multiple supported frameworks and runtimes such as Arrow Effects IO, KotlinX Coroutines Deferred, Rx2 Observable and many more.

Creating Typed Pure Functional Programs is fun and easy with Arrow Fx.
 
## Pure Functions, Side Effects and Program Execution

### Pure & Referentially Transparent Functions

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

### Side effects

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

##### `suspend` composition

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

##### `fx` composition

Side effects can be composed and turned into pure values in `fx` blocks. 

##### Turning side effects into pure values with `effect` 

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
    !pureHello
    !pureGoodBye
  }
//sampleEnd 
fun main() {
  println(greet())
}
```

When we capture suspended side effects as `IO` values with `effect`, we can pass them around and compose them until we are ready to apply the effects

##### Applying side effects with `!effect`

Side effects are applied with the operator `!`. Once you purify a side effect with `effect` you can extract it's value in a non blocking way. `!effect` takes the suspended side effects `sayHello()` and `sayGoodbye()` and ensures they are controlled by the `IO` context before they get a chance to be executed. This ensures our effect compositions are pure and referentially transparent and will only run as part of an `IO` program at the edge.
Note running `greet()` does not perform any effects because it's an `IO` value.
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

### Executing effectful programs

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

Arrow Fx programs are not restricted to `IO` but in fact polymorphic and would work unmodified in many useful runtimes like the ones we find in popular libraries such as KotlinX Coroutines `Deferred`, Rx2 `Observable`, Reactor framework `Flux` and in general any third party data type that can model sync and async effect suspension.

If you've come this far and you are not too familiar with FP you may have realized that despite the buzzwords and some FP jargon, you already know how to use Arrow Fx for the most part. That is because Arrow Fx brings the most popular imperative style to effectful programs with few simple primitives for effect control and asynchronous programming. 

## Asynchronous & Concurrent Programming

Arrow Fx benefits from auto-binding and direct syntax for asynchronous programming yielding extremely succinct programs without callbacks. This allow us to use direct style syntax with asynchronous and concurrent operations while preserving effect control in the types and runtime.

### Dispatchers and Contexts

Performing effects while switching execution contexts a la carte is trivial.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
import kotlinx.coroutines.newSingleThreadContext

//sampleStart
val contextA = newSingleThreadContext("A")

suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = fx {
  continueOn(contextA)
  !effect { printThreadName() }
  continueOn(NonBlocking)
  !effect { printThreadName() }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

In addition to `continueOn`, Arrow Fx allows users to override the executions context in all functions that require one.

### Fibers

A [Fiber](/docs/effects/fiber) represents the pure result of a [Concurrent] data type being started concurrently and that can be either `join`ed or `cancel`ed.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val fiberA = !NonBlocking.startFiber(effect { threadName() })
  val fiberB = !NonBlocking.startFiber(effect { threadName() })
  val threadA = !fiberA.join()
  val threadB = !fiberA.join()
  !effect { println(threadA) }
  !effect { println(threadB) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

When we spawn fibers we can obtain their deferred non-blocking result using `join()` and destructuring the effect.

`NonBlocking` is an execution context available to all concurrent data types such as IO that you can use directly on `fx` blocks.

Note that because we are using `Fiber` here and a Dispatcher that may not create new threads in all cases we are not guaranteed that the thread names printed would be different.

This is part of the greatness of Fibers. They run as scheduled based on the policies provided by the Dispatcher's Context.

### Parallelization & Concurrency

Arrow Fx comes with built in versions of `parMapN`, `parTraverse` and `parSequence` that allows users to dispatch effects in parallel and receive results non-blocking and direct syntax without wrappers. 

#### `parMapN`

`parMapN` allows *N#* effects to run in parallel non-blocking waiting for all results to complete and then it delegates to a user provided function that applies a final transformation over the results.
Once the function specifies a valid return we can observe how the returned non-blocking value is bound in the left hand side. 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name
  
data class ThreadInfo(
  val threadA : String, 
  val threadB: String
)

val program = fx {
  val (threadA: String, threadB: String) = 
    !NonBlocking.parMapN(
      effect { threadName() },
      effect { threadName() },
      ::ThreadInfo
    )
  !effect { println(threadA) }
  !effect { println(threadB) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

#### `parTraverse`

`parTraverse` allows any `Iterable<suspend () -> A>` to iterate over its contained effects in parallel as we apply a user provided function over each effect result and then gather all the transformed results in a `List<B>` 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val result: List<String> = !NonBlocking.parTraverse(
    listOf(
        effect { threadName() },
        effect { threadName() },
        effect { threadName() }
    )
  ) {
      "running on: $it" 
    }
  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

#### `parSequence`

`parSequence` applies all effects in `Iterable<suspend () -> A>` in non-blocking in parallel and then gathers all the transformed results and returns them in a `List<B>` 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx

//sampleStart
suspend fun threadName(): String =
  Thread.currentThread().name

val program = fx {
  val result: List<String> = !NonBlocking.parSequence(
    listOf(
      effect { threadName() },
      effect { threadName() },
      effect { threadName() }
    )
  )
  
  !effect { println(result) }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```

### Cancellation

All concurrent `fx` continuations are cancellable. Users may use the `fxCancellable` function to run `fx` blocks that beside returning a value it returns a disposable handler that can interrupt the operation.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fxCancellable
//sampleStart
val (_, disposable) = fxCancellable {
  !effect { println("BOOM!") }
}
//sampleEnd
fun main() { // The edge of our world
  println(disposable)
}
```

### Arrow Fx vs KotlinX Coroutines

In the same way Arrow is a companion to the Kotlin standard library providing the abstractions and runtime to implement Typed FP in Kotlin, Arrow Fx can be seen as a companion to the KotlinX Coroutines library.

Arrow Fx adds an extra layer of security and effect control where we can easily model side effects and how they interact with pure computations.

In contrast with the couroutines library where `Deferred` computations are eager by default and fire immediately when instantiated, in Arrow Fx, all bindings and compositions are lazy and suspended ensuring execution is explicit and always deferred until the last second.

Deferring execution and being able to suspend side effects is important for programs built with Arrow because we can ensure that effects run in a controlled environment and preserve the properties of purity and referential transparency that allows us to apply equational reasoning over the different parts that conform our programs.

Since Arrow Fx uses this lazy behavior by default we don't have to resort to special configuration arguments when creating deferred computations.

The value `program` below is pure and referentially transparent because `fx` returns a lazy computation. 

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

val program = fx {
  !effect { printThreadName() }
}

fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
//sampleEnd
```

The same with the default `async` constructor from the coroutines library will yield an impure function because effects are not controlled and they fire immediately upon function invocation:

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() = 
  async { printThreadName() }

fun main() { 
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

In the previous program `printThreadName()` may get invoked before we call `await`.
If we wanted a pure lazy version of this operation we need to hint the `async` constructor that our policy is to not start right away. 

```kotlin:ank:playground
import kotlinx.coroutines.*
import kotlin.system.*

//sampleStart
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

suspend fun program() = 
  async(start = CoroutineStart.LAZY) { printThreadName() }

fun main() { 
  runBlocking<Unit> { program().await() }
}
//sampleEnd
```

If an `async` computations fires immediately it does not give us a chance to suspend side effects. This implies that all functions that produce their effects immediately when invoked are impure and non-referentially transparent. This is the default in the KotlinX Coroutines Lib.

Arrow Fx is not opinionated as to whether firing eagerly is a more or less appropriate technique. We, the authors, understand this style gathers a different audience where purity and referential transparency may be non goals or optimizations techniques are in play and that is just fine. 

Life goes on.

Arrow Fx offers in contrast a different approach that is inline with Arrow's main concern which is helping you as a user create well-typed safe and pure programs in Kotlin.

On top of complementing the KolinX Coroutines api, Arrow Fx provides interoperability with its runtime allowing you to run polymorphic programs over the KotlinX Coroutines, Rx2, Reactor and even custom runtimes.

### Integrating with third party libraries

Arrow Fx integrates out of the box with the Arrow Effects IO runtime, KotlinX Coroutines, Rx2, Reactor framework and any library that models effectful async/concurrent computations and can provide a `@extension` to the `ConcurrentEffect<F>` type class defined in the `arrow-effects` module.

If you are interested in providing your own runtime as backend to the Arrow Fx library please contact us in the arrow main gitter or slack channels with any questions and we'll help you along the way.

## Polymorphism. One Program multiple runtimes

Fx programs can be declared in a polymorphic style and made concrete to your framework of choice at the edge of the world.

### Creating polymorphic programs

So far all programs we've created in previous examples where built with `Arrow`'s `IO`.
Fx is not restricted to `IO`. Since `IO` provides extensions for `Concurrent<F>` we can also create extensions for the `fx` DSL directly as in all previous examples.

We can also code agains't `Fx` assuming it would be provided at some point in the future.

In the following example the program is declared polymorphic and then made concrete to Arrow IO at the edge.

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
//sampleStart
/* a side effect */
suspend fun printThreadName(): Unit =
  println(Thread.currentThread().name)

/* for all `F` that provide an `Fx` extension define a program function
fun <F> Fx<F>.program(): Kind<F, Int> =
  fx { !effect { sideEffect() } }

/* for all `F` that provide an `UnsafeRun` extension define a main function
fun <F> UnsafeRun<F>.main(fx: Fx<F>): Int =
  unsafe { runBlocking { fx.program() } }

/* Run program in the IO monad */
fun main() =
  IO.unsafeRun().main(IO.fx()) 
//sampleEnd
```

Polymorphism is important for two main reasons: *Correctness* & *Flexibility*.

Polymorphic programs are more likely to be correct because the API's available to them is constrained by the functionality described in the type classes that are used as scope, in the previous example the receiver of the function. 
What's most amazing about this technique is that as we declare our programs only data types that are able to provide extensions for `Fx` and `UnsafeRun` are able to be provided as arguments to the final `main`. 
You will not be able to compile this program unless your data type supported those extensions.

Polymorphic programs are more flexible that their concrete counterparts because they can run unmodified in multiple runtimes.
In the same way `main` is concrete in the previous example to `IO`, we could have used there instead Rx2 Observables, Reactor Flux or any other data type that is able to provide extensions for `Fx` and `UnsafeRun`.

### Fx for all data types

Fx is not restricted to data types that support concurrency like `IO`. Other data types can utilize versions of the `Fx` dsl with reduced powers based on the level of constrains that they can provide extensions for.

The Arrow library already provides the ability to compute imperatively over all monads and brings first class do notation / comprehensions with an extremely elegant syntax thanks to Kotlin's operator overloading features and suspension system. The following examples demonstrate use of effect binding in two of the many possible data types in which `Fx` programs can compile to.

*Fx over `Option`*
```kotlin:ank:playground
import arrow.effects.IO
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

*Fx over `Try`*
```kotlin:ank:playground
import arrow.core.Try
import arrow.core.extensions.`try`.fx.fx

//sampleStart
val result = 
  fx {
    val (one) = Try { 1 }
    val (two) = Try { one + one }
    two
  }
//sampleEnd

fun main() {
  println(result)
}
```

The `component1` operator over all `Kind<F, A>` is able to obtain a non blocking `A` bound in the LHS in the same way `!` does.

```kotlin
suspend fun <A> Kind<F, A>.bind(): A
suspend operator fun <A> Kind<F, A>.component1(): A = bind()
suspend operator fun <A> Kind<F, A>.not(): A = bind()
```

The result is destructuring syntax for all monadic expressions by receiving the value bound in the left hand side in a non blocking fashion.

### Arrow Fx vs Tagless Final

Arrow Fx eliminates the need for tagless style algebras and parametrization over expressions that require `F` as a type parameter when invoking functions.
 
When modeling effects as `suspend` functions that are disallowed to compile in the pure environment but welcome in compositional blocks denoted as `effect { sideEffect() }`.

Arrow Fx brings first class, no-compromises direct style syntax for effectful programs that are constrained by monads that can provide an extension of `Concurrent<F>`.
 It models effects with the Kotlin Compiler native system support for `suspend` functions and abilities to declare restricted suspended blocks with `@RestrictsSuspension` in which effects are allowed to run and compose.
 
 We still preserve `F` to achieve polymorphism but it's usage it's restricted to type declarations and unnecessary in program composition.

 As a consequence of the assimilation and elimination of the `F` type parameter from the syntax in such programs the entire hierarchy of type class combinators we find in `Functor`, `Applicative` and `Monad` dissapear and what they model is swallowed as syntax that operates directly over the environment.

This simplification is manifested in the world of suspended effects in the fact that all values of type `Kind<F, A>` can bind to `A` in the left hand side in a non blocking fashion because Kotlin supports imperative CPS and continuation styles syntactically. 
Arrow Fx uses the Kotlin compiler native support for implicit CPS to achieve direct syntax for effectful monads.

This has a tremendous impact in program declaration since all the functional combinators where before you had as return type a `Kind<F, A>` can be easily applied with `!` to obtain a non blocking value. 
`map`, `flatMap` are no longer necessary because their returned values are flattened and automatically bound in the monad context when you use `!`

This leads us to realize that there is a some direct relationship between `suspend () -> A` and `Kind<F, A>` or what in Scala is `F[A]`.
This relationship establishes that a `suspend` function denoting an effect can be deferred and controlled by the monadic context of a suspend capable data type.

`effect` takes us without blocking semantics from a suspended function to any `Kind<F, A>`.
This includes IO and pretty much everything you are using today in a tagless final or IO wrapping style.

This relationship also eliminates the need to ever use any of the functional combinators you find in the `Functor<F>` hierarchy for effectful monads. 
All of them are swallowed by equivalent direct syntax in the environment as demonstrated in the examples below:

#### Good bye `Functor`, `Applicative` and `Monad`

`Arrow Fx` removes the need to use the functional combinators found in the Functor, Applicative and Monad type classes.

These combinators are instead represented as direct syntax and compile-time guaranteed by the Kotlin compiler that effectful computations denoted by the user can't run uncontrolled in functions denoted as pure.

The following combinators illustrate how the Functor hierarchy functions are pointless in the environment given we can declare programs that respect the same semantics thanks to the Kotlin suspension system. These below are examples of a few of the most well known combinators that would disappear from your day to day FP programming and what they look like in Arrow Fx:

| TypeClass           | Wrapped | Fx |
|---------------------|-----------|--------------------|
| Functor.map         | `just(1).map { it + 1 }` | `1 + 1`  |
| Applicative.just    | `just(1)` | `1` |
| Applicative.mapN    | `mapN(just(1), just(2), ::Tuple2)` | `1 toT 2` |
| Applicative.tupled  | `tupled(just(1), just(2))` | `1 toT 2` |
| Monad.flatMap       | `IO.just(1).flatMap { n -> IO { n + 1 } }` | `1 + 1` |
| Monad.flatten       | `IO.just(IO.just(1))}.flatten()` | `1` |
| MonadDefer.delay    | `IO.delay { 1 }` | `effect { 1 }` |
| MonadDefer.defer    | `IO.defer { IO { 1 } }` | `effect { 1 }` |

This is in general true for effectful data types that are non-commutative. 
Note that implicit CPS style with auto-binding has the disadvantage that for non-commutative monads where the order of effects matter you can't apply substitution based on referential transparency.

Arrow Fx is aware of this but still allows users to use `fx` on non-commutative monads such as `List` providing safe `fx` builders that guarantee suspended effects are applied in order in different arguments before they are composed.
Altering the order of effect when using the safe builders for commutative monads does not alter the result

```kotlin:ank:playground
import arrow.core.identity
import arrow.core.toT
import arrow.data.extensions.list.fx.fx

//sampleStart
val result1 = fx(listOf(1, 2), listOf(true, false), ::identity)
val result2 = fx(listOf(true, false), listOf(1, 2), ::identity)
//sampleEnd

fun main() {
  println(result1)
  println(result2)
}
```

Arrow identifies autobinding in these non-commutative monads as unsafe and an as an effect worth tracking!.
For consistency if you want to shoot yourself in the foot, performing auto binding for these non-commutative types requires the user to give explicit permission to activate the unsafe `fx` block.

```kotlin:ank:playground
import arrow.unsafe
import arrow.core.toT
import arrow.data.extensions.list.fx.fx

//sampleStart
val result1 = unsafe { 
  fx {
    val (a) = listOf(1, 2)
    val (b) = listOf(true, false)
    a toT b
  }
}

val result2 = unsafe { 
  fx {
    val (b) = listOf(true, false)
    listOf(1, 2) toT b
  }
}
//sampleEnd

fun main() {
  println(result1)
  println(result2)
}
```

The previous program shows how applying substitution alters the order of effects and affects the outcome.

The same program expressed in a commutative monad which is the case of `IO` shows how both programs yield the same deterministic result even after changing the order of effects:

```kotlin:ank:playground
import arrow.effects.IO
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.fx.fx
import arrow.core.toT

//sampleStart
val result1 = 
  fx {
    val a = !effect { 1 }
    val b = !effect { 2 }
    a toT b
  }

val result2 = 
  fx {
    val b = !effect { 2 }
    !effect { 1 } toT b
  }
//sampleEnd

fun main() {
  println(result1)
  println(result2)
}
```

# Conclusion

Arrow Fx is a next generation Typed FP Effects Library that makes effectful and polymorphic programing first class in Kotlin acting as an extension to the Kotlin native suspend system.
Complementing the Kotlin Coroutines library, Arrow Fx adds an extra layer of safety to concurrent and asynchronous programming making you well aware were effects are localized in your apps.
It does all this by empowering polymorphic programs that can be interpreted untouched preserving the same declaration in multiple popular frameworks such as Rx2, Reactor, etc.

Arrow Fx offers direct style syntax and effect control without compromises and removes the syntactic burden of type parametrization while still yielding programs that are pure, safe and referentially transparent.

Despite some of the Kotlin type system limitations like lack of Higher Kinded Types, The Kotlin language is excellent to encode typed programs with first class imperative syntax for FP. These programs are approachable by the broader programming community and arguably easier to encode than some solutions currently used in mainstream Kotlin and Scala FP communities.

Come check out Arrow fx and join the discussion in the Kotlin Slack and Gitter at [arrow-kt.io]()
