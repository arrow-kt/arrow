---
layout: docs-fx
title: Arrow Fx - Polymorphism. One Program, multiple runtimes
permalink: /docs/fx/polymorphism/
---

# Polymorphism. One Program, multiple runtimes

Fx programs can be declared in a polymorphic style and made concrete to your framework of choice at the edge of the world.

## Creating polymorphic programs

So far, the programs we've created in previous examples were built with `Arrow`'s `IO`, but Fx is not restricted to `IO`. Since `IO` provides extensions for `Concurrent<F>`, we can also create extensions for the `fx` DSL directly as in all previous examples.

We can also code against `Fx`, assuming it would be provided at some point in the future.

In the following example, the program is declared polymorphic, and then made concrete to Arrow IO at the edge.

```kotlin:ank:playground
import arrow.fx.IO
import arrow.unsafe
import arrow.Kind
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.io.unsafeRun.unsafeRun
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.fx
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.UnsafeRun

//sampleStart
/* a side effect */
val const = 1
suspend fun sideEffect(): Int {
  println(Thread.currentThread().name)
  return const
}

/* for all `F` that provide an `Fx` extension define a program function */
fun <F> Concurrent<F>.program(): Kind<F, Int> =
  fx.concurrent { !effect { sideEffect() } }

/* for all `F` that provide an `UnsafeRun` extension define a main function */
fun <F> UnsafeRun<F>.main(fx: Concurrent<F>): Int =
  unsafe { runBlocking { fx.program() } }

/* Run program in the IO monad */
fun main(args: Array<String>) {
    IO.unsafeRun().main(IO.concurrent())
}
//sampleEnd
```

Polymorphism is important for two main reasons: *Correctness* & *Flexibility*.

Polymorphic programs are more likely to be correct because the APIs available to them are constrained by the functionality described in the type classes that are used as a scope. In the previous example, this is the receiver of the function.
What's most amazing about this technique is that, as we declare our programs, only data types that can provide extensions for `Fx` and `UnsafeRun` are able to be provided as arguments to the final `main`.
You will not be able to compile this program unless your data type supports those extensions.

Polymorphic programs are more flexible than their concrete counterparts because they can run unmodified in multiple runtimes.
In the same fashion that `main` is concrete in the previous example to `IO`, we could have used Rx2 Observables, Reactor Flux, or any other data type that is able to provide extensions for `Fx` and `UnsafeRun` instead.
See [Issue 1281](https://github.com/arrow-kt/arrow/issues/1281), which tracks support for those frameworks, or reach out to us if you are interested in support for any other framework.

## Fx for all data types

Fx is not restricted to data types that support concurrency like `IO`. Other data types can utilize versions of the `Fx` DSL with reduced powers based on the level of constrains that they can provide extensions for.

The Arrow library already provides the ability to compute imperatively over all monads, and brings first-class do-notation / comprehensions with an extremely elegant syntax thanks to Kotlin's operator overloading features and suspension system. The following examples demonstrate the use of effect binding with two of the many options of data types in which `Fx` programs can compile to.

*Fx over `Option`*
```kotlin:ank:playground
import arrow.fx.IO
import arrow.core.Option
import arrow.core.extensions.fx

//sampleStart
val result = Option.fx {
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
import arrow.core.extensions.fx

//sampleStart
val result =
  Try.fx {
    val (one) = Try { 1 }
    val (two) = Try { one + one }
    two
  }
//sampleEnd

fun main() {
  println(result)
}
```

The `component1` operator over all `Kind<F, A>` is able to obtain a non-blocking `A` bound in the LHS in the same way `!` does.

```kotlin
suspend fun <A> Kind<F, A>.bind(): A
suspend operator fun <A> Kind<F, A>.component1(): A = bind()
suspend operator fun <A> Kind<F, A>.not(): A = bind()
```

The result destructures syntax for all monadic expressions by receiving the value bound in the left-hand side in a non-blocking fashion.

## Arrow Fx vs Tagless Final

Arrow Fx eliminates the need for tagless style algebras and parametrization over expressions that require `F` as a type parameter when invoking functions.

When modeling effects as `suspend`, functions that are disallowed to compile in the pure environment, but welcome in compositional blocks, are denoted as `effect { sideEffect() }`.

Arrow Fx brings first class, no-compromises——direct style syntax for effectful programs that are constrained by monads that can provide an extension of `Concurrent<F>`.
 It models effects with the Kotlin Compiler's native system support for `suspend` functions, and the ability to declare restricted suspended blocks with `@RestrictsSuspension` in which effects are allowed to run and compose.

 We still preserve `F` to achieve polymorphism, but its usage is restricted to type declarations, and unnecessary in program composition.

 As a consequence of the assimilation and elimination of the `F` type parameter from the syntax in such programs, the entire hierarchy of type class combinators we find in `Functor`, `Applicative`, and `Monad` disappear. And what they model is swallowed as syntax that operates directly over the environment.

This simplification is manifested in the world of suspended effects in the fact that all values of type `Kind<F, A>` can bind to `A` in the left-hand side in a non-blocking fashion because Kotlin supports imperative CPS and continuation styles syntactically.
Arrow Fx uses the Kotlin compiler native support for implicit CPS to achieve direct syntax for effectful monads.

This has a tremendous impact on program declaration since all the functional combinators can be easily applied with `!` to obtain a non blocking value, where before you had `Kind,F, A>` as a return type.
`map` and `flatMap` are no longer necessary because their returned values are flattened and automatically bound in the monad context when you use `!`.

This leads us to the realization that there is a direct relationship between `suspend () -> A` and `Kind<F, A>`, or, what `F[A]` is in Scala.
This relationship establishes that a `suspend` function denoting an effect can be deferred and controlled by the monadic context of a suspend capable data type.

`effect` takes us, without blocking semantics, from a suspended function to any `Kind<F, A>`.
This includes IO and pretty much everything you are using today in a tagless final or IO wrapping style.

This relationship also eliminates the need for using any of the functional combinators you find in the `Functor<F>` hierarchy for effectful monads.
These are all swallowed by equivalent direct syntax in the environment, as demonstrated in the examples below:

### Goodbye `Functor`, `Applicative`, and `Monad`

`Arrow Fx` removes the need to use the functional combinators found in the Functor, Applicative, and Monad type classes.

These combinators are instead represented as direct syntax and compile-time, and guaranteed by the Kotlin compiler that effectful computations denoted by the user can't run uncontrolled in functions denoted as pure.

The following combinators illustrate how the Functor hierarchy functions are pointless in the environment, given we can declare programs that respect the same semantics thanks to the Kotlin suspension system. Below, we'll see examples of a few of the most well-known combinators that will disappear from your day-to-day FP programming, and what they look like in Arrow Fx:

| TypeClass           | Wrapped | Fx |
|---------------------|-----------|--------------------|
| Functor.map         | `just(1).map { it + 1 }` | `1 + 1`  |
| Applicative.just    | `just(1)` | `1` |
| Applicative.mapN    | `mapN(just(1), just(2), ::Tuple2)` | `1 toT 2` |
| Applicative.tupled  | `tupled(just(1), just(2))` | `1 toT 2` |
| Monad.flatMap       | `IO.just(1).flatMap { n -> IO { n + 1 } }` | `1 + 1` |
| Monad.flatten       | `IO.just(IO.just(1))}.flatten()` | `1` |
| MonadDefer.later    | `IO.later { 1 }` | `effect { 1 }` |
| MonadDefer.defer    | `IO.defer { IO { 1 } }` | `effect { 1 }` |
| Async.effect        | `IO.effect { 1 }` | `effect { 1 }` |

This is, in general, true for effectful data types that are commutative.
