---
layout: docs
title: ApplicativeError
permalink: /docs/typeclasses/applicativeerror/
---

## ApplicativeError

ApplicativeError is the typeclase used to explicitly represent errors during independent computations.
It is parametrized to an error type `E`, which means the datatype has at least a "success" and a "failure" version.

These errors can come in the form of `Throwable`, `Exception`, or any other type that is more relevant to the domain;
like for example a sealed class UserNotFoundReason that contains 3 inheritors.

Some of the datatypes Λrrow provides can have these error types already fixed.
That's the case of [`Try<A>`]({{ '/docs/datatypes/try' | relative_url }}), which has its error type fixed to `Throwable`.
Other datatypes like [`Either<E, A>`]({{ '/docs/datatypes/either' | relative_url }}) allow for the user to apply their error type of choice.

### Main Combinators

`ApplicativeError` inherits all the combinators available in [`Applicative`]({{ '/docs/typeclasses/applicative' | relative_url }}). It also adds several of its own.

#### raiseError

A constructor function. It lifts an exception into the computational context of a type constructor.

```kotlin:ank
import arrow.*
import arrow.core.*

Either.applicativeError<Throwable>().raiseError<Int>(RuntimeException("Paco"))
```

```kotlin:ank
import arrow.data.*

Try.applicativeError().raiseError<Int>(RuntimeException("Paco"))
```

```kotlin:ank
import arrow.effects.*

IO.applicativeError().raiseError<Int>(RuntimeException("Paco"))
```

#### handleErrorWith

This method requires a function that creates a new datatype from an error, `(E) -> HK<F, A>`. This function is used as a catch + recover clause for the current instance, allowing it to return a new computation after a failure.

If [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) has `flatMap` to allow mapping the value inside a *successful* datatype into a new datatype, you can think of `handleErrorWith` as a way that allows you to map the value of a *failed datatype into a new datatype. 

```kotlin:ank
val AE_EITHER = Either.applicativeError<Throwable>()

val success: Either<Throwable, Int> = Either.Right(1)

AE_EITHER.handleErrorWith(success, { t -> Either.Right(0) })
```

```kotlin:ank
val failure: Either<Throwable, Int> = Either.Left(RuntimeException("Boom!"))

AE_EITHER.handleErrorWith(failure, { t -> Either.Right(0) })
```

#### handleError

Similar to `handleErrorWith`, except the function can return any regular value. This value will be wrapped and used as a return.

```kotlin:ank
AE_EITHER.handleError(success, { t -> 0 })
```

```kotlin:ank
AE_EITHER.handleError(failure, { t -> 0 })
```

#### attempt

Maps the current content of the datatype to an [`Either<E, A>`]({{ '/docs/datatypes/either' | relative_url }}), recovering from any previous error state.

```kotlin:ank
val AE_TRY = Try.applicativeError()

val pass = Try { "3".toInt() }
pass
```

```kotlin:ank
AE_TRY.attempt(pass)
```

```kotlin:ank
val fail = Try { "nope".toInt() }
fail
```

```kotlin:ank
AE_TRY.attempt(fail)
```

#### fromEither

Constructor function from an [`Either<E, A>`]({{ '/docs/datatypes/either' | relative_url }}) to the current datatype.

```kotlin:ank
AE_TRY.fromEither(Either.Right(1))
```

```kotlin:ank
AE_TRY.fromEither(Either.Left(RuntimeException("Boom")))
```

#### catch

Constructor function. It takes two function parameters. The first is a generator function from `() -> A`. The second is an error mapping function from `(Throwable) -> E`.
`catch()` runs the generator function to generate a success datatype, and if it throws an exception it uses the error mapping function to create a new failure datatype.

```kotlin:ank
AE_EITHER.catch({ 1 } ,{ it })
```

```kotlin:ank
AE_EITHER.catch({ throw RuntimeException("Boom") } ,{ it })
```

### Laws

Arrow provides [`ApplicativeErrorLaws`]({{ '/docs/typeclasses/laws#applicativeerrorlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `ApplicativeError` instances.

### Data types

The following datatypes in Arrow provide instances that adhere to the `ApplicativeError` typeclass.

- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [Kleisli]({{ '/docs/datatypes/kleisli' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
- [StateT]({{ '/docs/datatypes/statet' | relative_url }})
- [IO]({{ '/docs/effects/io' | relative_url }})
- [ObservableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [FlowableKW]({{ '/docs/integrations/rx2' | relative_url }})
- [DeferredKW]({{ '/docs/integrations/kotlinxcoroutines/' | relative_url }})
