---
layout: docs-core
title: Functional Error Handling
permalink: /patterns/error_handling/
---

## Functional Error Handling

When dealing with errors in a purely functional way, we try as much as we can to avoid exceptions.
Exceptions break referential transparency and lead to bugs when callers are unaware that they may happen until it's too late at runtime.

In the following example, we are going to model a basic program and go over the different options we have for dealing with errors in Arrow.
The program simulates the typical lunch scenario where we have to get the ingredient, and a series of preconditions needs to be met in order to actually prepare and eat it.

### Requirements

- Take food out of the refrigerator
- Get your cutting tool
- Cut up the lettuce to make lunch

### Requirements

```kotlin
/** model */
object Lettuce
object Knife
object Salad

fun takeFoodFromRefrigerator(): Lettuce = TODO()
fun getKnife(): Knife = TODO()
fun prepare(tool: Knife, ingredient: Lettuce): Salad = TODO()
```

### Exceptions

A naive implementation that uses exceptions may look like this

```kotlin
fun takeFoodFromRefrigerator(): Lettuce = throw RuntimeException("You need to go to the store and buy some ingredients")
fun getKnife(): Knife = throw RuntimeException("Your knife needs to be sharpened")
fun prepare(tool: Knife, ingredient: Lettuce): Salad = Salad
```

As you may have noticed, the function signatures include no clue that, when asking for `takeFoodFromRefrigerator()` or `getKnife()`,
an exception may be thrown.

#### The issues with exceptions

Exceptions can be seen as GOTO statement, given they interrupt the program flow by jumping back to the caller.
Exceptions are not consistent, as throwing an exception may not survive async boundaries; that is to say that one can't rely on exceptions for error handling
in async code, since invoking a function that is async inside a `try/catch` may not capture the exception potentially thrown in a different thread.

Because of this extreme power of stopping computation and jumping to other areas, Exceptions have been abused even in core libraries to signal events.

```
at java.lang.Throwable.fillInStackTrace(Throwable.java:-1)
at java.lang.Throwable.fillInStackTrace(Throwable.java:782)
- locked <0x6c> (a sun.misc.CEStreamExhausted)
at java.lang.Throwable.<init>(Throwable.java:250)
at java.lang.Exception.<init>(Exception.java:54)
at java.io.IOException.<init>(IOException.java:47)
at sun.misc.CEStreamExhausted.<init>(CEStreamExhausted.java:30)
at sun.misc.BASE64Decoder.decodeAtom(BASE64Decoder.java:117)
at sun.misc.CharacterDecoder.decodeBuffer(CharacterDecoder.java:163)
at sun.misc.CharacterDecoder.decodeBuffer(CharacterDecoder.java:194)
```

They often lead to incorrect and dangerous code because `Throwable` is an open hierarchy where you may catch more than you originally intended to.

```kotlin
try {
  doExceptionalStuff() //throws IllegalArgumentException
} catch (e: Throwable) { 
    // too broad, `Throwable` matches a set of fatal exceptions and errors a 
   // a user may be unable to recover from:
    /*
    VirtualMachineError
    OutOfMemoryError
    ThreadDeath
    LinkageError
    InterruptedException
    ControlThrowable
    NotImplementedError
    */
}
```

Furthermore, exceptions are costly to create. `Throwable#fillInStackTrace` attempts to gather all stack information to present you with a meaningful stacktrace.

```java
public class Throwable {
    /**
    * Fills in the execution stack trace.
    * This method records within this Throwable object information
    * about the current state of the stack frames for the current thread.
    */
    Throwable fillInStackTrace();
}
```

Constructing an exception may be as costly as your current Thread stack size, and it's also platform dependent since `fillInStackTrace` calls into native code.

More info on the cost of instantiating Throwables, and throwing exceptions in general, can be found in the links below.

> [The Hidden Performance costs of instantiating Throwables](http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/)

Exceptions may be considered generally a poor choice in Functional Programming when:

- Modeling absence
- Modeling known business cases that result in alternate paths
- Used in async boundaries over APIs based callbacks that lack some form of structured concurrency.
- In general, when people have no access to your source code.

### How do we model exceptional cases then?

Arrow and the Kotlin standard library provides proper datatypes and abstractions to represent exceptional cases.

### Nullable types

We use [`Nullable types`](https://kotlinlang.org/docs/null-safety.html#nullable-types-and-non-null-types) to model the potential absence of a value.

When using `Nullable types`, our previous example may look like:

```kotlin
fun takeFoodFromRefrigerator(): Lettuce? = null
fun getKnife(): Knife? = null
fun prepare(tool: Knife, ingredient: Lettuce): Salad? = Salad
```

It's easy to work with [`Nullable types`](https://kotlinlang.org/docs/null-safety.html#nullable-types-and-non-null-types) if your lang supports special syntax like `?` as Kotlin does. 
Nullable types are faster than boxed types like `Option`. Nonetheless `Option` is also supported by Arrow to interop with Java based libraries that use `null` as signal or interruption value like [ReactiveX RxJava](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#nulls). Additionally `Option` is useful in generic code when not constraining with generic bounds of `A : Any` and using null as a nested signal to produce values of `Option<Option<A>>` since A? can't have double nesting.

```kotlin
import arrow.core.computations.nullable

fun prepareLunch(): Salad? {
  val lettuce = takeFoodFromRefrigerator()
  val knife = getKnife()
  val salad = knife?.let { k -> lettuce?.let { l -> prepare(k, l) } }
  return salad
}
```

In addition to `let` provided by the standard library Arrow provides `nullable` which allows the use of [Computation Expressions]({{ '/patterns/monad_comprehensions' | relative_url }}).


```kotlin
import arrow.core.computations.nullable

suspend fun prepareLunch(): Salad? =
  nullable {
    val lettuce = takeFoodFromRefrigerator().bind()
    val knife = getKnife().bind()
    val salad = prepare(knife, lettuce).bind()
    salad
  }
```

While we could model this problem using `Nullable Types`, and forgetting about exceptions, we are still unable to determine the reasons why `takeFoodFromRefrigerator()` and `getKnife()` returned empty values in the form of `null`.
For this reason, using `Nullable Types` is only a good idea when we know that values may be absent, but we don't really care about the reason why.
Additionally, `Nullable Types` are unable to capture exceptions. If an exception was thrown internally, it would still bubble up and result in a runtime exception.

In the next example, we are going to use `Either` to deal with potentially thrown exceptions that are outside the control of the caller.

### Either

When dealing with a known alternate path, we model return types as [`Either`]({{ '/apidocs/arrow-core/arrow.core/-either/' | relative_url }})
Either represents the presence of either a `Left` value or a `Right` value.
By convention, most functional programming libraries choose `Left` as the exceptional case and `Right` as the success value.

It turns out that all exceptions thrown in our example are actually known to the system, so there is no point in modeling these exceptional cases as
`java.lang.Exception`.

We should redefine our functions to express that their result is not just a `Lettuce`, `Knife`, or `Salad`, but those potential values or other exceptional ones.

We can now assign proper types and values to the exceptional cases.

```kotlin
sealed class CookingException {
  object NastyLettuce: CookingException()
  object KnifeIsDull: CookingException()
  data class InsufficientAmountOfLettuce(val quantityInGrams : Int): CookingException()
}
typealias NastyLettuce = CookingException.NastyLettuce
typealias KnifeIsDull = CookingException.KnifeIsDull
typealias InsufficientAmountOfLettuce = CookingException.InsufficientAmountOfLettuce
```

This type of definition is commonly known as an Algebraic Data Type or Sum Type in most FP capable languages.
In Kotlin, it is encoded using sealed hierarchies. We can think of sealed hierarchies as a declaration of a type and all its possible construction states.

Once we have an ADT defined to model our known errors, we can redefine our functions.

```kotlin
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right

fun takeFoodFromRefrigerator(): Either<NastyLettuce, Lettuce> = Right(Lettuce)
fun getKnife(): Either<KnifeIsDull, Knife> = Right(Knife)
fun lunch(knife: Knife, food: Lettuce): Either<InsufficientAmountOfLettuce, Salad> = Left(InsufficientAmountOfLettuce(5))
```

Arrow also provides an `Effect` instance for `Either` in the same way it did for `Nullable types`.
Except for the types signatures, our program remains unchanged when we compute over `Either`.
All values on the left side assume to be `Right` biased and, whenever a `Left` value is found, the computation short-circuits, producing a result that is compatible with the function type signature.

```kotlin
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either

suspend fun prepareEither(): Either<CookingException, Salad> =
  either {
    val lettuce = takeFoodFromRefrigerator().bind()
    val knife = getKnife().bind()
    val salad = lunch(knife, lettuce).bind()
    salad
  }
```

### Alternative validation strategies : Failing fast vs accumulating errors

In this different validation example, we demonstrate how we can use `mapAccumulating` to perform error accumulation strategies vs short-circuit ones.

```kotlin
import arrow.core.Nel
import arrow.core.computations.either
import arrow.core.handleErrorWith
import arrow.typeclasses.Semigroup
import arrow.core.zip
```

*Model*

```kotlin
sealed class ValidationError(val msg: String) {
  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)
```

*Strategies*

```kotlin
/** strategies **/
sealed class Strategy {
  object FailFast : Strategy()
  object ErrorAccumulation : Strategy()
}

/** Abstracts away invoke strategy **/
object Rules {

  private fun FormField.contains(needle: String): EitherNel<ValidationError, FormField> =
    if (value.contains(needle, false)) rightNel()
    else ValidationError.DoesNotContain(needle).leftNel()

  private fun FormField.maxLength(maxLength: Int): EitherNel<ValidationError, FormField> =
    if (value.length <= maxLength) rightNel()
    else ValidationError.MaxLength(maxLength).leftNel()

  private fun FormField.validateErrorAccumulate(): EitherNel<ValidationError, Email> =
    contains("@").zip(
      maxLength(250)
    ) { _, _ -> Email(value) }.handleErrorWith { ValidationError.NotAnEmail(it).leftNel() }

  /** either blocks support binding over Validated values with no additional cost or need to convert first to Either **/
  private fun FormField.validateFailFast(): EitherNel<ValidationError, Email> =
    either {
      contains("@").bind() // fails fast on first error found
      maxLength(250).bind()
      Email(value)
    }

  operator fun invoke(strategy: Strategy, fields: List<FormField>): EitherNel<ValidationError, List<Email>> =
    when (strategy) {
      Strategy.FailFast -> either { fields.map { it.validateFailFast().bind() } }
      Strategy.ErrorAccumulation -> fields.mapAccumulating { it.validateErrorAccumulate() }
    }
}
```

*Program*

```kotlin
val fields = listOf(
    FormField("Invalid Email Domain Label", "nowhere.com"),
    FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails 
    FormField("Valid Email Label", "getlost@nowhere.com")
)
```

*Fail Fast*

```kotlin
Rules(Strategy.FailFast, fields)
```

*Error Accumulation*

```kotlin
Rules(Strategy.ErrorAccumulation, fields)
```

### Credits

Tutorial adapted from the 47 Degrees blog [`Functional Error Handling`](https://www.47deg.com/presentations/2017/02/18/Functional-error-handling/)
