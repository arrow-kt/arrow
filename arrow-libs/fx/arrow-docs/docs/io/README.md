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

## Why `suspend () -> A` instead of `IO<A>`

This section explains the rationale about why Arrow dropped `IO` in favor of `suspend`.

The reason for using `IO` is because you care about writing side-effecting code in a safe and referential transparent manner.
Additionally, `IO` offers powerful concurrent operators and cancellation in addition of offering a referential transparent runtime.
These properties are what makes using `IO` powerful, and `suspend` offers the exact same properties but natively in the language with support from the compiler. 

Below we discuss a couple topics why we favor `suspend` over `IO`.

### Ergonomics

`IO` requires a wrapper in the return type: `fun number(): IO<Int>`, and thus we always have to work with the `IO` type to access the value we care about within.
A typical pattern for this using `flatMap`, so let's say we want to calculate 3 numbers and return them as a `Triple`.

```kotlin
fun number(): IO<Int> = IO.just(1)

fun triple(): IO<Triple<Int, Int, Int>> =
  number().flatMap { a ->
    number().flatMap { b ->
      number().map { c ->
        Triple(a, b, c)
      }
    }
  }
```

So simply to call a function 3 times, and combine the result into a `Triple` we had to use `flatMap` twice and `map`.
What that means under the hood we'll discuss in the performance section but in terms of ergonomics this is not ideal.
Especially not if we can compare it to the following `suspend` code:

```kotlin
suspend fun number(): Int = 1

suspend fun triple(): Triple<Int, Int, Int> = 
  Triple(number(), number(), number())
```

Here we can see that we can simply forget about `flatMap` & `map` and we can simply construct the `Triple` and call `number()` three times directly in the constructor.

The ergonomics of `suspend` are clearly better here, and this is a very important point in Kotlin since the language aims for high ergonomics and developer friendly constructs.

### Safety / Purity / Referential transparency

So what guarantees does `IO` bring, and does `suspend` offer the same guarantees?

`IO<A>` always results in `Either<Throwable, A>` whether we run it with `unsafeRunSync` (throws Throwable), `unsafeRunAsync` or `unsafeRunAsyncCancellable`.
This is done so that any exceptions that occur within the `IO` API can be safely returned to the user, and it can be recovered from at any point in the code.
Important here is that a `Throwable` that occurs in an async thread is safely captured in the `IO` as well and can be recovered from at any point.

`suspend` always results in `Result<A>` which is isomorphic to `Either<Throwable, A>`, and it can be used to offer the same safety guarantees as `IO`.
So the `suspend` API can also always return any exception safely to the user, and it can be recovered from at any point in the code.
In contrast to `IO`, we can only find `startCoroutine` in the standard library and it's isomorphic to `unsafeRunAsync`.
Instead of `f: (Either<Throwable, A>) -> Unit` you provide `f: (Result<A>) -> Unit` to run the `suspend () -> A` program.

So `suspend () -> A` offers us the exact same guarantees as `IO<A>`.

Both also forces us to go through certain set of methods to execute/run the effectful program.

### Monad transformers vs effect mixing

#### IO<Either<E, A>> vs suspend () -> Either<E, A>

When writing functional code style we often want to express our domain errors as clearly as possible, a popular pattern is to return `Either<DomainError, SuccessValue>`.
Let's assume following domain, and compare two snippets one using `IO<Either<E, A>>` and another `suspend () -> Either<E, A>`.

```kotlin:ank
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

/* inline */ class Id(val id: Long)
object PersistenceError

data class User(val email: String, val name: String)
data class ProcessedUser(val id: Id, val email: String, val name: String)

suspend fun fetchUser(): Either<PersistenceError, User> =
  Right(User("simon@arrow-kt.io", "Simon"))

suspend fun User.process(): Either<PersistenceError, ProcessedUser> =
  if (email.contains(Regex("^(.+)@(.+)$"))) Right(ProcessedUser(Id(1), email, name))
  else Left(PersistenceError)
```

###### IO<Either<E, A>>

 ```kotlin:ank
import arrow.fx.*

fun ioProgram(): IO<Either<PersistenceError, ProcessedUser>> =
  IO.fx {
    val res = !IO.effect { fetchUser() }

    !res.fold({ error ->
      IO.just(Either.Left(error))
    }, { user ->
      IO.effect { user.process() }
    })
  }

// Or unwrapped in `suspend`
suspend suspendedIOProgram(): Either<PersistenceError, ProcessedUser> =
  ioProgram().suspended()
 ```

##### suspend () -> Either<E, A>

```kotlin:ank
import arrow.core.computations.either

suspend fun suspendProgram(): Either<PersistenceError, ProcessedUser> =
  either {
    val user = !fetchUser()
    val processed = !user.process()
    processed
  }
```
The above two examples demonstrate how much simpler `suspend` is over its `IO` counterpart and how the `either` computation block allows us to bind values of `Either` to extract their right side all while inside `suspend`.

Arrow Fx allows to intermix effects in suspension. What otherwise would have required the `EitherT` transformer over `IO` now it can just be expressed by wrapping in `either` instead
#### suspend R.() -> A

We can use extension functions to do functional dependency injection with similar semantics as `Reader` or `Kleisli`.
They allow us to elegantly define `syntax` for a certain type. Let's see a simple example.

Let's reuse our previous domain of`User`, `ProcessedUser`, but let's introduce `Repo` and `Persistence` layers to mimick what could be a small app with a couple layers.

```kotlin:ank
interface Repo {
    suspend fun fetchUsers(): List<User>
}

interface Persistence {
    suspend fun User.process(): Either<PersistenceError, ProcessedUser>

    suspend fun List<User>.process(): Either<PersistenceError, List<ProcessedUser>> =
        either { map { !it.process() } }
}
```

Given the above defined layers we can easily compose them by creating a product which implements the dependencies by delegation.

```kotlin:ank
class DataModule(
    persistence: Persistence,
    repo: Repo
) : Persistence by persistence, Repo by repo
```

We can also define top-level functions based on constraints on the receiver.
Here we define `getProcessUsers` which can only be called where `R` is both `Repo` and `Persistence`.

```kotlin:ank
/**
 * Generic top-level function based on syntax enabled by [Persistence] & [Repo] constraint
 */
suspend fun <R> R.getProcessUsers(): Either<PersistenceError, List<ProcessedUser>>
        where R : Repo,
              R : Persistence = fetchUsers().process()
```

### Performance

`suspend` is extremely fast in comparison to `IO<A>`, since `IO<A>` is build at runtime and `suspend` is build by the compiler.

Working with an ADT such as `IO<A>` implies that each composition of our program has some allocation cost.
This happens because the IO ADT uses different data classes to move computation from the stack to the heap in order to compose them and preserve properties such as lazy evaluation semantics.

In contrast, when using `suspend`, the Kotlin compiler is aware of function composition on each suspension point and can desugar and specialise the program into more efficient target code. The generated code by the Kotlin compiler is better in terms of allocations and throughput when compared to other IO ADT based impls in the JVM.

Let's take our previous example from ergonomics:
 
```kotlin:ank
import arrow.fx.IO

fun number(): IO<Int> = IO.just(1)

fun triple(): IO<Triple<Int, Int, Int>> =
  number().flatMap { a ->
    number().flatMap { b ->
      number().map { c ->
        Triple(a, b, c)
      }
    }
  }
```

If we translate this piece of code to the `data class` it uses it results in the following:

```kotlin
fun number(): IO<Int> = IO.Just(1)

fun triple(): IO<Triple<Int, Int, Int>> =
  IO.Bind(IO.Just(1)) { a ->
    IO.Bind(IO.Just(1)) { b ->
      IO.Map(IO.Just(1)) { c -> Triple(a, b, c) }
    }
  }
```

We can see that for this simple program we allocated 6 `IO` cases of the `IO` `sealed class`.
This is necessary so when `unsafeRun` is invoked the `IO` program can find the branch representing the kind of operation of `IO` that needs to be interpreted. In the example above `IO.Bind`, `IO.Map` or `IO.Just`

In contrast, `suspend` can simply be wired by the Kotlin compiler eliminating the need for additional `sealed class` declarations and allocations keeping computations in the stack instead of maintaining value level in memory representations of our program. 
The Kotlin compiler rewrites the suspend program to a super fast runtime which uses a switch table and mutable state machine to run the `suspend` program.
Furthermore, the Kotlin compiler applies other optimisations focused on the speed of `suspend` and memory usage of suspension making it suitable for hotspots and places where otherwise allocations or ADT based data types are not an option.
