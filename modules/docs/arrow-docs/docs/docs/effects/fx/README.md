---
layout: docs
title: Fx
permalink: /docs/effects/fx/
---

## Arrow Fx. Typed FP for the masses

Arrow Fx is a library that brings purity, referential transparency and direct syntax for effectful programs denoted as suspended functions.

Arrow Fx opens a world of purity and controlled effects to help you describe your favorite effectful programs in both polymorphic and concrete styles as you do today using a wrapper like IO, Deferred or Observable but with direct syntax and unwrapped values that respect non-blocking semantics.

Arrow Fx removes the need for all the combinators in the Functor hierarchy empowering programmers to express equivalent programs for all suspended capable runtimes like Kotlinx Coroutines, Reactor Framework, Rx2, etc bridging the gap of FP features a user must learn before
is productive defining algebras and interpreters.

The below program illustrates the *Hello World* of effectful programming with Arrow Fx:
 
## Fx `Hello World`

### Pure functions

Pure functions return deterministically the same output given the same input and do not alter the state of the outer
world. A pure expression can be defined and it's allowed to run in the Kotlin raw environment by the Kotlin compiler.

```kotlin:ank
val helloWorld: String =
  "Hello World"
  
helloWorld
```

### Side effects

Users can denote side effects and in general effectful statements and expressions with `suspend fun`.

```kotlin:ank
suspend fun sayHello(): Unit =
  println(helloWorld)
```

A `suspend` function denotes a suspended computation. All side effects should be suspended and non-evaluated in 
compositions to ensure purity and referential transparency of the effectful expressions.

Attempting to run a side effect in the pure environment is disallowed by the Kotlin compiler unless we prove that
we know how to handle success and error outcomes from evaluating the effect once suspension is resumed. 

```kotlin:ank:fail
sayHello()
```

The Kotlin compiler won't allow side effects denoted as `suspend` to run or compile until you provide a `Continuation<A>`
that handles success and error cases.

The Kotlin compiler automatically translates all suspended functions to a function that receives a `kotlin.coroutines.Continuation<A>` additional argument bridging this way CPS style continuation without callbacks.

### Composing effects and pure expressions

It's fine though applying and composing side effects in the presence of an `fx` block. 
`fx` is continuation that all data types providing an extension for `ConcurrentEffect<F>` can support.

The example below proves how our effect controlled program can run in the context of the IO monad which Arrow provides
while it fails to compile in all other positions due to being an uncontrolled invocation.

```kotlin:ank:fail
import arrow.effects.extensions.io.fx.fx

fx {
  sayHello() // Does not compile because Arrow Fx forces uses to denote side effects in `effect` blocks
}
val x = sayHello() // Does not compile because suspended functions need to be in a continuation
```

```kotlin:ank
import arrow.effects.extensions.io.fx.fx

val program = fx {
  effect { sayHello() } 
}

program
```

### Executing effectful programs

All programs encapsulated in `fx` block yield a value at the end wrapped in the concurrent data type the user wishes
to make its program concrete to. 
This allows `Arrow Fx` to support as many possible run times as suspend capable monad data types exist.

This implies Arrow Fx programs are not restricited to IO but in fact polymorphic and would work unmodified in many useful runtimes like the ones we find in popular libraries such as KotlinX Coroutines `Deferred`,
Rx2 `Observable`, Reactor framework `Flux` and in general any third party data type that can model sync and async effect suspension.

The `program` above is ready to run as soon as the user is ready to commit to an execution strategy that will decide if the program runs
blocking or non-blocking. Since both blocking and non-blocking execution scenarios perform the side effects contained i the program description we consider running effects an `unsafe` operation. 

Running effectful computations is restricted to runtime extensions of the `Unsafe` type class which `IO` provides extensions for.
Usage of `unsafe` is reserved to the end of the world and may be the only impure execution of a well typed functional program:

```kotlin:ank
import arrow.unsafe
import arrow.effects.extensions.io.unsafeRun.runBlocking

unsafe { runBlocking { program } }
```

Arrow Fx makes emphasis in guaranteeing users understand where they are performing side effects in their program declaration.

If you've come this far and you are not too familiar with FP you are probably realizing by now that you already know how to use Arrow Fx
for the most part. That is because Arrow Fx brings the most popular imperative style to effectful programs. A style that most OOP developers coming from other backgrounds are already accustomed to. 

## Fx eliminates the need for `F` parameterized tagless style programs.

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
fun <A> (suspend () -> A).k(): Kind<F, A> =
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

`k()` takes us without blocking semantics from a suspended function to any `Kind<F, A>` for which an `Async<F>` extension exists.
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
  map { effect { f(it()) } }
```

# Conclusion

The Arrow Fx system offers direct style syntax and effect control without compromises and removes the syntactic burden of type parametrization while still yielding polymorphic programs that are pure, safe and referentially transparent.

Despite some the Kotlin type system limitations like lack of Higher Kinded Types, The Kotlin language is more than capable of producing
purely typed programs with first class imperative syntax for FP that is approachable by the broader programming community.

Come check out Arrow fx and join the discussion in the Kotlin Slack and Gitter at [arrow-kt.io]()
