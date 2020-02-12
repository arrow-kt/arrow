---
layout: docs-core
title: Functional Error Handling
permalink: /docs/patterns/error_handling/
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

```kotlin:ank
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

```kotlin:ank
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
} catch (e: Throwable) { //too broad matches:
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
    Throwable fillInStackTrace()
}
```

Constructing an exception may be as costly as your current Thread stack size, and it's also platform dependent since `fillInStackTrace` calls into native code.

More info on the cost of instantiating Throwables, and throwing exceptions in general, can be found in the links below.

> [The Hidden Performance costs of instantiating Throwables](http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/)
> * New: Creating a new Throwable each time
> * Lazy: Reusing a created Throwable in the method invocation.
> * Static: Reusing a static Throwable with an empty stacktrace.

Exceptions may be considered generally a poor choice in Functional Programming when:

- Modeling absence
- Modeling known business cases that result in alternate paths
- Used in async boundaries over unprincipled APIs (callbacks)
- In general, when people have no access to your source code

### How do we model exceptional cases then?

Arrow provides proper datatypes and typeclasses to represent exceptional cases.

### Option

We use [`Option`](/docs/arrow/core/option) to model the potential absence of a value.

When using `Option`, our previous example may look like:

```kotlin:ank
import arrow.*
import arrow.core.*

fun takeFoodFromRefrigerator(): Option<Lettuce> = None
fun getKnife(): Option<Knife> = None
fun prepare(tool: Knife, ingredient: Lettuce): Option<Salad> = Some(Salad)
```

It's easy to work with [`Option`](/docs/apidocs/arrow-core-data/arrow.core/-option/) if your lang supports [Monad Comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) or special syntax for them.
Arrow provides [monadic comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }})  for all datatypes for which a [`Monad`](/docs/arrow/typeclasses/monad) instance exists built atop coroutines.

```kotlin
import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.option.monad.binding

fun prepareLunchOption(): Option<Salad> =
  fx.monad {
    val lettuce = takeFoodFromRefrigerator().bind()
    val knife = getKnife().bind()
    val salad = prepare(knife, lettuce).bind()
    salad
  }

prepareLunchOption()
//None
```

While we could model this problem using `Option`, and forgetting about exceptions, we are still unable to determine the reasons why `takeFoodFromRefrigerator()` and `getKnife()` returned empty values in the form of `None`.
For this reason, using `Option` is only a good idea when we know that values may be absent, but we don't really care about the reason why.
Additionally, `Option` is unable to capture exceptions. So, if an exception was thrown internally, it would still bubble up and result in a runtime exception.

In the next example, we are going to use `Either` to deal with potentially thrown exceptions that are outside the control of the caller.

### Either

When dealing with a known alternate path, we model return types as [`Either`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }})
Either represents the presence of either a `Left` value or a `Right` value. 
By convention, most functional programming libraries choose `Left` as the exceptional case and `Right` as the success value.

It turns out that all exceptions thrown in our example are actually known to the system, so there is no point in modeling these exceptional cases as
`java.lang.Exception`.

We should redefine our functions to express that their result is not just a `Lettuce`, `Knife`, or `Salad`, but those potential values or other exceptional ones.

We can now assign proper types and values to the exceptional cases.

```kotlin:ank
sealed class CookingException {
    object LettuceIsRotten: CookingException()
    object KnifeNeedsSharpening: CookingException()
    data class InsufficientAmount(val quantityInGrams : Int): CookingException()
}

typealias NastyLettuce = CookingException.LettuceIsRotten
typealias KnifeIsDull = CookingException.KnifeNeedsSharpening
typealias InsufficientAmountOfLettuce = CookingException.InsufficientAmount
```

This type of definition is commonly known as an Algebraic Data Type or Sum Type in most FP capable languages.
In Kotlin, it is encoded using sealed hierarchies. We can think of sealed hierarchies as a declaration of a type and all its  possible states.

Once we have an ADT defined to model our known errors, we can redefine our functions.

```kotlin:ank
fun takeFoodFromRefrigerator(): Either<NastyLettuce, Lettuce> = Right(Lettuce)
fun getKnife(): Either<KnifeIsDull, Knife> = Right(Knife)
fun lunch(knife: Knife, food: Lettuce): Either<InsufficientAmountOfLettuce, Salad> = Left(InsufficientAmountOfLettuce(5))
```

Arrow also provides a `Monad` instance for `Either` in the same way it did for `Option`.
Except for the types signatures, our program remains unchanged when we compute over `Either`.
All values on the left side assume to be `Right` biased and, whenever a `Left` value is found, the computation short-circuits, producing a result that is compatible with the function type signature.

```kotlin
import arrow.core.extensions.either.monad.binding

fun prepareEither(): Either<CookingException, Salad> =
  fx.monad {
    val lettuce = takeFoodFromRefrigerator().bind()
    val knife = getKnife().bind()
    val salad = lunch(knife, lettuce).bind()
    salad
  }

prepareEither()
//Left(InsufficientAmountOfLettuce(5))
```

So far, we have seen how we can use `Option` and `Either` to handle exceptions in a purely functional way.

The question now is, can we further generalize error handling and write this code in a way that is abstract from the actual datatypes that it uses?
Since Arrow supports typeclasses, emulated higher kinds, and higher order abstractions, we can rewrite this in a fully polymorphic way thanks to [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }})

### MonadError

[`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}) is a typeclass that allows us to handle error cases inside monadic contexts such as the ones we have seen with `Either` and `Option`.
Typeclasses allows us to code focusing on the behaviors, and not the datatypes that implement them.

Arrow provides the following `MonadError` instances for `Option` and `Either`

```kotlin:ank
import arrow.core.extensions.option.monadError.*

Option.monadError()
```

```kotlin:ank
import arrow.core.extensions.either.monadError.*

Either.monadError<CookingException>()
```

Let's now rewrite our program as a polymorphic function that will work over any datatype for which a `MonadError` instance exists.
Polymorphic code in Arrow is based on emulated `Higher Kinds`, as described in [Lightweight higher-kinded polymorphism](https://www.cl.cam.ac.uk/~jdy22/papers/lightweight-higher-kinded-polymorphism.pdf) and applied to Kotlin, a lang which does not yet support Higher Kinded Types.

```kotlin
fun <F> MonadError<F, CookingException>.takeFoodFromRefrigerator(): Kind<F, Lettuce> = just(Lettuce)
fun <F> MonadError<F, CookingException>.getKnife(): Kind<F, Knife> = just(Knife)
fun <F> MonadError<F, CookingException>.lunch(knife: Knife, food: Lettuce):
        Kind<F, Salad> = raiseError(InsufficientAmountOfLettuce(5))
```

We can now express the same program as before in a fully polymorphic context

```kotlin
fun <F> MonadError<F, CookingException>.prepare():Kind<F, Salad> =
    fx.monad {
        val lettuce = takeFoodFromRefrigerator<F>().bind()
        val knife = getKnife<F>().bind()
        val salad = lunch<F>(knife, lettuce).bind()
        salad
    }
```

Or, since `takeFoodFromRefrigerator()` and `getKnife()` are operations that do not depend on each other, we don't need the [Monad Comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) here, and we can express our logic as:

```kotlin
fun <F> MonadError<F, CookingException>.prepare1(): Kind<F, Salad> =
    tupledN(getKnife(), takeFoodFromRefrigerator()).flatMap({ (nuke, target) -> lunch<F>(nuke, target) })

val result = Either.monadError<CookingException>().prepare()
result.fix()
//Left(InsufficientAmountOfLettuce(5))
// or
val result1 = Either.monadError<CookingException>().prepare1()
result1.fix()
```

Note that `MonadThrow` also has a function `fx.monadThrow` that automatically captures and wraps exceptions in its binding block.

```kotlin
fun <F> MonadError<F, CookingException>.lunchImpure(target: Knife, nuke: Lettuce): Salad {
    throw InsufficientAmountOfLettuce(5)
}

fun <F> MonadError<F, CookingException>.prepare(): Kind<F, Salad> =
    fx.monadThrow {
        val lettuce = takeFoodFromRefrigerator<F>().bind()
        val knife = getKnife<F>().bind()
        val salad = lunchImpure<F>(knife, lettuce).bind()
        salad
    }
```

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

`Rules` defines abstract behaviors that can be composed and have access to the scope of `ApplicativeError`, where we can invoke `just` to lift values into the positive result and `raiseError` into the error context.

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

### Credits

Tutorial adapted from the 47 Degrees blog [`Functional Error Handling`](https://www.47deg.com/presentations/2017/02/18/Functional-error-handling/)

Deck:

- https://speakerdeck.com/raulraja/functional-error-handling
- https://github.com/47deg/functional-error-handling
