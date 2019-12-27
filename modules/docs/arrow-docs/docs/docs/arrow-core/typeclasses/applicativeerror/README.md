---
layout: docs-core
title: ApplicativeError
permalink: /docs/arrow/typeclasses/applicativeerror/
redirect_from:
  - /docs/typeclasses/applicativeerror/
---

## ApplicativeError




`ApplicativeError` is the typeclass used to explicitly represent errors during independent computations.
It is parametrized to an error type `E`, which means the datatype has at least a "success" and a "failure" version.

These errors can come in the form of `Throwable`, `Exception`, or any other type that is more relevant to the domain;
a sealed class UserNotFoundReason that contains three inheritors, for example.

Some of the datatypes Λrrow provides can have these error types already fixed.
That's the case with [`Try<A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-try/' | relative_url }}), which has its error type fixed to `Throwable`.
Other datatypes like [`Either<E, A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }}) allow for the user to apply their error type of choice.

### Main Combinators

`ApplicativeError` inherits all the combinators available in [`Applicative`]({{ '/docs/arrow/typeclasses/applicative' | relative_url }}). It also adds several of its own.

#### raiseError

A constructor function. It lifts an exception into the computational context of a type constructor.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.core.extensions.either.applicativeError.*

Either.applicativeError<Throwable>().raiseError<Int>(RuntimeException("Paco"))
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.`try`.applicativeError.*

Try.applicativeError().raiseError<Int>(RuntimeException("Paco"))
```

```kotlin:ank
import arrow.fx.*
import arrow.fx.extensions.io.applicativeError.*

IO.applicativeError().raiseError<Int>(RuntimeException("Paco"))
```

#### Kind<F, A>#handleErrorWith

This method requires a function that creates a new datatype from an error, `(E) -> Kind<F, A>`. This function is used as a catch + recover clause for the current instance, allowing it to return a new computation after a failure.

If [`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}) has `flatMap` to allow mapping the value inside a *successful* datatype into a new datatype, you can think of `handleErrorWith` as a way that allows you to map the value of a *failed datatype into a new datatype.

```kotlin:ank
import arrow.core.handleErrorWith

val success: Either<Throwable, Int> = Either.Right(1)

success.handleErrorWith { t -> Either.Right(0) }
```

```kotlin:ank
val failure: Either<Throwable, Int> = Either.Left(RuntimeException("Boom!"))

failure.handleErrorWith { t -> Either.Right(0) }
```

#### Kind<F, A>#handleError

Similar to `handleErrorWith`, except the function can return any regular value. This value will be wrapped and used as a return.

```kotlin:ank
success.handleError { t -> 0 }
```

```kotlin:ank
failure.handleError { t -> 0 }
```

#### Kind<F, A>#attempt

Maps the current content of the datatype to an [`Either<E, A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }}), recovering from any previous error state.

```kotlin:ank
Try { "3".toInt() }.attempt()
```

```kotlin:ank
Try { "nope".toInt() }.attempt()
```

#### fromEither/fromTry/fromOption

Constructor function from an [`Either<E, A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }}), [`Option<A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}), or [`Try<A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-try/' | relative_url }}) to the current datatype.

While `fromOption()` requires creating a new error value.

```kotlin:ank
Either.applicativeError<Throwable>().run { Some(1).fromOption { RuntimeException("Boom") } }
```

In the case of `fromTry()`, converting from `Throwable` to the type of the error is required.

```kotlin:ank
Either.applicativeError<String>().run { Try { RuntimeException("Boom") }.fromTry { it.message!! } }
```

In the case of `fromEither()`, converting from the error type of the `Either<EE, A>` to the type of the ApplicativeError<F, E> is required. 

```kotlin:ank
IO.applicativeError().run { Either.Right(1).fromEither { it } }
```

```kotlin:ank
IO.applicativeError().run { Either.Left(RuntimeException("Boom")).fromEither { it } }
```

#### catch

Constructor function. It takes two function parameters. The first is a generator function from `() -> A`. The second is an error mapping function from `(Throwable) -> E`.
`catch()` runs the generator function to generate a success datatype, and if it throws an exception, it uses the error mapping function to create a new failure datatype.

```kotlin:ank
val eitherAE = Either.applicativeError<Throwable>()

eitherAE.catch(::identity) { 1 }
```

```kotlin:ank
eitherAE.catch(::identity) { throw RuntimeException("Boom") }
```

### Laws

Arrow provides `ApplicativeErrorLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `ApplicativeError` instances.

### Example : Alternative validation strategies using `ApplicativeError`

In this validation example, we demonstrate how we can use `ApplicativeError` instead of `Validated` to abstract away validation strategies and raise errors in the context we are computing in.

*Model*

```kotlin
import arrow.*
import arrow.core.*
import arrow.typeclasses.*

sealed class ValidationError(val msg: String) {
  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)
```

*Rules*

```kotlin
sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {

  private fun FormField.contains(needle: String): Kind<F, FormField> =
    if (value.contains(needle, false)) just(this)
    else raiseError(ValidationError.DoesNotContain(needle).nel())

  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
    if (value.length <= maxLength) just(this)
    else raiseError(ValidationError.MaxLength(maxLength).nel())

  fun FormField.validateEmail(): Kind<F, Email> =
    map(contains("@"), maxLength(250), {
      Email(value)
    }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }

  object ErrorAccumulationStrategy :
    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

  object FailFastStrategy :
    Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())

  companion object {
    infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
    infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
  }

}
```

`Rules` defines abstract behaviors that can be composed and have access to the scope of `ApplicativeError` where we can invoke `just` to lift values into the positive result and `raiseError` into the error context.

Once we have such abstract algebra defined, we can simply materialize it to data types that support different error strategies:

*Error accumulation*

```kotlin
Rules accumulateErrors {
  listOf(
    FormField("Invalid Email Domain Label", "nowhere.com"),
    FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this accumulates N errors
    FormField("Valid Email Label", "getlost@nowhere.com")
  ).map { it.validateEmail() }
}
```
*Fail Fast*

```kotlin
Rules failFast {
  listOf(
    FormField("Invalid Email Domain Label", "nowhere.com"),
    FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails fast
    FormField("Valid Email Label", "getlost@nowhere.com")
  ).map { it.validateEmail() }
}
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.ApplicativeError

TypeClass(ApplicativeError::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.ApplicativeError)
