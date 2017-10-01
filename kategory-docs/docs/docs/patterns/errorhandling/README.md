---
layout: docs
title: Functional Error Handling
permalink: /docs/patterns/error_handling/
---

# Functional Error Handling

When dealing with errors in a purely functional way we try as much as we can to avoid exceptions.
Exceptions break referential transparency and lead to bugs when callers are unaware that they may happen until it's too late at runtime.

In the following example we are going to model a basic program and go over the different options we have for dealing with errors in Kategory.
The program simulates the typical game scenario where we have to shoot a target and series of preconditions need to be met in order actually shot and hit it.

## Requirements

- Arm a Nuke launcher
- Aim toward a Target
- Launch a Nuke and impact the Target

## Requirements

```kotlin:ank
/** model */
object Nuke
object Target
object Impacted

fun arm(): Nuke = TODO()
fun aim(): Target = TODO()
fun launch(target: Target, nuke: Nuke): Impacted = TODO()
```

## Exceptions

A naive implementation that uses exceptions may look like this

```kotlin:ank
fun arm(): Nuke = throw RuntimeException("SystemOffline")
fun aim(): Target = throw RuntimeException("RotationNeedsOil")
fun launch(target: Target, nuke: Nuke): Impacted = Impacted
```

As you may have noticed the function signatures include no clue that when asking for `arm()` or `aim()`
an exception may be thrown.

### The issues with exceptions

Exceptions can be seen as GOTO statement given they interrupt the program flow by jumping back to the caller.
Exceptions are not consistent as throwing an exception may not survive async boundaries, that is to say that one can't rely on exceptions for error handling
in async code since invoking a function that is async inside a `try/catch` may not capture the exception potentially thrown in a different thread.

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

They often lead to incorrect and dangerous code because `Throwable` is an open hierarchy where you may catch more than you originally intend to.

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

Furthermore exceptions are costly to create. `Throwable#fillInStackTrace` attempts to gather all stack information to present you with a meaningful stacktrace.

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

Constructing an exception may be as costly as your current Thread stack size and it's also platform dependent since `fillInStackTrace` calls into native code.

More info in the cost of instantiating Throwables and throwing exceptions in generals can be found in the links below.

> [The Hidden Performance costs of instantiating Throwables](http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/)
> * New: Creating a new Throwable each time
> * Lazy: Reusing a created Throwable in the method invocation.
> * Static: Reusing a static Throwable with an empty stacktrace.

Exceptions may be considered generally a poor choice in Functional Programming when:

- Modeling absence
- Modeling known business cases that result in alternate paths
- Used in async boundaries over unprincipled APIs (callbacks)
- In general when people have no access to your source code

## How do we model exceptional cases then?

Kategory provide proper datatypes and typeclasses to represent exceptional cases.

## Option ##

We use [`Option`](/docs/datatypes/option) to model the potential absence of a value

When using `Option` our previous example may look like:

```kotlin:ank
import kategory.*
import kategory.Option.*

fun arm(): Option<Nuke> = None
fun aim(): Option<Target> = None
fun launch(target: Target, nuke: Nuke): Option<Impacted> = Some(Impacted)
```

It's easy to work with [`Option`]({{ '/docs/datatypes/option' | relative_url }}) if your lang supports monad comprehensions or special syntax for them.
Kategory provides monadic comprehensions for all datatypes for which a [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) instance exists built atop corutines.

```kotlin
fun attackOption(): Option<Impacted> =
  Option.monad().binding {
    val nuke = arm().bind()
    val target = aim().bind()
    val impact = launch(target, nuke).bind()
    yields(impact)
  }.ev()

attackOption()
//None
```

While we could model this problem using `Option` and forgetting about exceptions we are still unable to determine the reasons why `arm()` and `aim()` returned empty values in the form of `None`.
For this reason using `Option` is only a good idea when we know that values may be absent but we don't really care about the reason why.
Additionally `Option` is unable to capture exceptions so if an exception was thrown internally it would still bubble up and result in a runtime exception.

In the next example we are going to use `Try` to deal with potentially thrown exceptions that are outside the control of the caller.

## Try ##

We use [`Try`]({{ '/docs/datatypes/try' | relative_url }}) when we want to be defensive about a computation that may fail with a runtime exception

How would our example look like implemented with `Try`?

```kotlin:ank
fun arm(): Try<Nuke> =
  Try { throw RuntimeException("SystemOffline") }

fun aim(): Try<Target> =
  Try { throw RuntimeException("RotationNeedsOil") }

fun launch(target: Target, nuke: Nuke): Try<Impacted> =
  Try { throw RuntimeException("MissedByMeters") }
```

As you can see by the examples below exceptions are now controlled and caught inside of a `Try`.

```kotlin:ank
arm()
```

```kotlin:ank
aim()
```

Unlike in the `Option` example here we can fold over the resulting value accessing the runtime exception.

```kotlin:ank
val result = arm()
result.fold({ ex -> "BOOM!: $ex"}, { "Got: $it" })
```

Just like it does for `Option`, Kategory also provides `Monad` instances for `Try` and we can use it exactly in the same way

```kotlin
fun attackTry(): Try<Impacted> =
  Try.monad().binding {
    val nuke = arm().bind()
    val target = aim().bind()
    val impact = launch(target, nuke).bind()
    yields(impact)
  }.ev()

attackTry()
//Failure(RuntimeException("SystemOffline"))
```

While `Try` gives us the ability to control both the `Success` and `Failure` cases there is still nothing in the function signatures that indicate the type of exception.
We are still subject to guess what the exception is using Kotlin `when` expressions or runtime lookups over the unsealed hierarchy of Throwable.

It turns out that all exceptions thrown in our example are actually known to the system so there is no point in modeling these exceptional cases as
`java.lang.Exception`

We should redefine our functions to express that their result is not just a `Nuke`, `Target` or `Impact` but those potential values or other exceptional ones.

## Either ##

When dealing with a known alternate path we model return types as [`Either`]({{ '/docs/datatypes/either' | relative_url }})
Either represents the presence of either a `Left` value or a `Right` value.
By convention most functional programing libraries choose `Left` as the exceptional case and `Right` as the success value.

We can now assign proper types and values to the exceptional cases.

```kotlin:ank
sealed class NukeException {
  object SystemOffline: NukeException()
  object RotationNeedsOil: NukeException()
  data class MissedByMeters(val meters : Int): NukeException()
}

typealias SystemOffline = NukeException.SystemOffline
typealias RotationNeedsOil = NukeException.RotationNeedsOil
typealias MissedByMeters = NukeException.MissedByMeters
```

This type of definition is commonly known as an Algebraic Data Type or Sum Type in most FP capable languages.
In Kotlin it is encoded using sealed hierarchies. We can think of sealed hierarchies as a declaration of a type and all it'
s possible states.

Once we have an ADT defined to model our known errors we can redefine our functions.

```kotlin:ank
import kategory.Either.*

fun arm(): Either<SystemOffline, Nuke> = Right(Nuke)
fun aim(): Either<RotationNeedsOil, Target> = Right(Target)
fun launch(target: Target, nuke: Nuke): Either<MissedByMeters, Impacted> = Left(MissedByMeters(5))
```

Kategory also provides a `Monad` instance for `Either` in the same it did for `Option` and `Try`.
Except for the types signatures our program remains unchanged when we compute over `Either`.
All values on the left side assume to be `Right` biased and whenever a `Left` value is found the computation short-circuits producing a result that is compatible with the function type signature.

```kotlin
fun attackEither(): Either<NukeException, Impacted> =
  Either.monad<NukeException>().binding {
    val nuke = arm().bind()
    val target = aim().bind()
    val impact = launch(target, nuke).bind()
    yields(impact)
  }.ev()

attackEither()
//Left(MissedByMeters(5))
```

We have seen so far how we can use `Option`, `Try` and `Either` to handle exceptions in a purely functional way.

The question now is, can we further generalize error handling and write this code in a way that is abstract from the actual datatypes that it uses.
Since Kategory supports typeclasses, emulated higher kinds and higher order abstractions we can rewrite this in a fully polymorphic way thanks to [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }})

## MonadError

[`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) is a typeclass that allows us to handle error cases inside monadic contexts such as the ones we have seen with `Either`, `Try` and `Option`.
Typeclasses allows us to code focusing on the behaviors and not the datatypes that implements them.

Kategory provides the following `MonadError` instances for `Option`, `Try` and `Either`

```kotlin:ank
monadError<OptionHK, Unit>()
```

```kotlin:ank
monadError<TryHK, Throwable>()
```

```kotlin:ank
monadError<EitherKindPartial<NukeException>, NukeException>()
```

Let's now rewrite our program as a polymorphic function that will work over any datatype for which a `MonadError` instance exists.
Polymorphic code in Kategory is based on emulated [`Higher Kinds`](/docs/patterns/polymorphicprograms) as described in [Lightweight higher-kinded polymorphism](https://www.cl.cam.ac.uk/~jdy22/papers/lightweight-higher-kinded-polymorphism.pdf) and applied to Kotlin, a lang which does not yet support Higher Kinded Types.

```kotlin
inline fun <reified F> arm(ME: MonadError<F, NukeException> = monadError()): HK<F, Nuke> = ME.pure(Nuke)
inline fun <reified F> aim(ME: MonadError<F, NukeException> = monadError()): HK<F, Target> = ME.pure(Target)
inline fun <reified F> launch(ME: MonadError<F, NukeException> = monadError()): HK<F, Impacted> = ME.raiseError(MissedByMeters(5))
```

We can know express the same program as before in a fully polymorphic context

```kotlin
inline fun <reified F> attack(ME: MonadError<F, NukeException> = monadError()): HK<F, Impacted> =
  monadError.bindingE {
    val nuke = arm().bind()
    val target = aim().bind()
    val impact = launch(target, nuke).bind()
    yields(impact)
  }
```

Or since `arm()` and `bind()` are operations that do not depend on each other we don't need the monad comprehensions here and we can express our logic as:

```kotlin
inline fun <reified F> attack(ME: MonadError<F, NukeException> = monadError()): HK<F, Impacted> =
  ME.tupled(aim(), arm()).flatMap({ (nuke, target) -> launch(nuke, target) })

val result = attack<EitherKindPartial<NukeException>>()
result.ev()
//Left(MissedByMeters(5))
```

## Credits

Tutorial adapted from the 47 Degrees blog [`Functional Error Handling`](https://www.47deg.com/presentations/2017/02/18/Functional-error-handling/)

Deck:

- https://speakerdeck.com/raulraja/functional-error-handling
- https://github.com/47deg/functional-error-handling
