---
layout: docs-core
title: MonadError
permalink: /docs/arrow/typeclasses/monaderror/
redirect_from:
  - /docs/typeclasses/monaderror/
---

## MonadError




MonadError is the typeclass used to explicitly represent errors during sequential execution.
It is parametrized to an error type `E`, which means the datatype has at least a "success" and a "failure" version.
These errors can come in the form of `Throwable`, `Exception`, or any other type hierarchy of the user's choice.

`MonadError` extends from [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror' | relative_url }}), which is already used to represent errors in independent computations. This way, all the methods [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror' | relative_url }}) provides to handle recovery from errors are also available in `MonadError`.

### Main Combinators

`MonadError` inherits all the combinators available in [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror' | relative_url }}) and [`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}). It also adds one of its own.

#### raiseError

Inherited from [`ApplicativeError`]({{ '/docs/arrow/typeclasses/applicativeerror' | relative_url }}). A constructor function.
It lifts an exception into the computational context of a type constructor.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.core.extensions.either.applicativeError.*

val eitherResult: Either<Throwable, Int> =
  RuntimeException("BOOM!").raiseError()

eitherResult
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.`try`.applicativeError.*

val tryResult: Try<Int> =
  RuntimeException("BOOM!").raiseError()

tryResult
```

```kotlin:ank
import arrow.fx.*
import arrow.fx.extensions.io.applicativeError.*

val ioResult: IO<Nothing, Int> =
  RuntimeException("BOOM!").raiseError()

ioResult.attempt().unsafeRunSync()
```

#### Kind<F, A>.ensure

Tests a predicate against the object, and, if it fails, it executes a function to create an error.

```kotlin:ank
import arrow.core.extensions.either.monadError.*

Either.Right(1).ensure({ RuntimeException("Failed predicate") }, { it > 0 })
```

```kotlin:ank
Either.Right(1).ensure({ RuntimeException("Failed predicate") }, { it < 0 })
```

### Comprehensions

#### fx.monadThrow

It starts a [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) that wraps any exception thrown in the block inside `raiseError()`.

### Laws

Arrow provides `MonadErrorLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadError` instances.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.MonadError

TypeClass(MonadError::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.MonadError)
