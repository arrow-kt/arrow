# Semantics of Structured Concurrency + Cont<R, A>

KotlinX Structured Concurrency is super important for eco-system,  and thus important to us for wide-adoption of this pattern

There are two options we can take to signal `shift` cancelled the Coroutine.
Using `kotlin.coroutines.CancellationException`, or `arrow.continuations.generics.ControlThrowable`
as the baseclass of our "ShiftedException".

Below we're going to discuss the scenarios of `context(CoroutineScope, ContEffect<E>)`,
thus the mixing the effects of Structured Concurrency with Continuation Shifting.

Short recap:
 - `launch` launches a **new** coroutine that gets cancelled when it's outer `CoroutineScope` gets cancelled,
    and it will re-throw any uncaught unexpected exceptions. Unless an `CoroutineExceptionHandler` is installed.
    => This excludes `CancellationException`.

 - `async` launches a **new** coroutine that gets cancelled when it's outer `CoroutineScope` gets cancelled,
   and it will re-throw any uncaught unexpected exceptions. If you do not call `await` it will not re-throw `CancellationException`.

## Scenario 1 (launch):

```kotlin
import arrow.core.identity
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

measureTimeMillis {
  cont<String, Int> {
    coroutineScope {
      val fa = launch { shift("error") }
      val fb = launch { delay(2000) }
      fa.join() // With or without `join` has no effect on semantics
      fb.join() // With or without `join` has no effect on semantics
      1
    }
  }
    .fold(::identity, ::identity)
    .let { println("Result is: $it") }
}.let { println("Took $it milliseconds") }
```

In this case, we have 2 independent parallel tasks running within the same `CoroutineScope`.
When we shift from inside `val fa = launch { shift(error) }` this will attempt to short-circuit the `Continuation` created by `suspend Cont<R, A>.fold`.

#### CancellationException

`CancellationException` is a special case, and is meant so signal cancellation,
so an internal cancellation of `fa`, will not cancel `fb` or the parent scope.
```
Result is: 1
Took 2141 milliseconds
```
So with `CancellationException` this snippet *doesn't* see the `shift`.
It allows `fb` to finish, which means the whole scope takes 2000 milliseconds,
and it successfully returns `1`.

This is probably a quite unexpected result,
what happened here is that `launch` swallowed `CancellationException` since a job that cancelled itself doesn't require further action.
If `fa` had other children `Job`s**, then those would've been cancelled.

#### ControlThrowable

Since `ControlThrowable` is seen as a regular exception by Kotlin(X) Coroutines, `launch` will rethrow the exception.
```
Result is: error
Took 95 milliseconds
```
This will cancel `fb`, and `coroutineScope` will also re-throw the exception.
This means that our `cont { }` DSL receives the exception, and thus we successfully short-circuit the `Continuation` created by `suspend Cont<R, A>.fold`.

## Scenario 2 (async { }.await())

```kotlin
import arrow.core.identity
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.async

measureTimeMillis {
  cont<String, Int> {
    coroutineScope {
      val fa = async<Int> { shift("error") }
      val fb = async { delay(2000); 1 }
      fa.await() + fb.await()
    }
  }.fold(::identity, ::identity)
    .let { println("Result is: $it") }
}.let { println("Took $it milliseconds") }
```

In this case, we have 2 deferred values computing within the same `CoroutineScope`.
When we shift from inside `val fa = async { shift(error) }` this will attempt to short-circuit the `Continuation` created by `suspend Cont<R, A>.fold`.
We **explicitly** await the result of `async { }`.
```
Result is: error
Took 3 milliseconds
```
Due to the call to `await` it will rethrow `CancellationException`, or any other exception.
So this scenario behaves the same for both `CancellationException` and `ControlThrowable`.

## Scenario 2 (async { })

```kotlin
import arrow.core.identity
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.async

measureTimeMillis {
  cont<String, Int> {
    coroutineScope {
      val fa = async<Int> { shift("error") }
      val fb = async { delay(2000); 1 }
      -1
    }
  }.fold(::identity, ::identity)
    .let { println("Result is: $it") }
}.let { println("Took $it milliseconds") }
```

In this case, we have 2 deferred values computing within the same `CoroutineScope`.
When we shift from inside `val fa = async { shift(error) }` this will attempt to short-circuit the `Continuation` created by `suspend Cont<R, A>.fold`.
We **don't** await the result of `async { }`.

#### CancellationException

`CancellationException` is a special case, and is meant so signal cancellation,
so an internal cancellation of `fa`, will not cancel `fb` or the parent scope **if** we don't rely on the result.
```
Result is: -1
Took 2008 milliseconds
```
So with `CancellationException` this snippet *doesn't* see the `shift`.
It allows `fb` to finish, which means the whole scope takes 2000 milliseconds,
and it successfully returns `-1`.

This is probably a quite unexpected result,
what happened here is that `async` didn't rethrow `CancellationException` since we never relied on the result.
If `fa` had other children `Job`s**, then those would've been cancelled.

#### ControlThrowable

Since `ControlThrowable` is seen as a regular exception by Kotlin(X) Coroutines, `async` will rethrow the exception.
```
Result is: error
Took 3 milliseconds
```
This will cancel `fb`, and `coroutineScope` will also re-throw the exception.
This means that our `cont { }` DSL receives the exception, and thus we successfully short-circuit the `Continuation` created by `suspend Cont<R, A>.fold`.

** Job: A Job represents a single running Coroutine, and it holds references to all its children `Job`s.

# Conclusion

It seems that continuing to use `ControlThrowable` for shifting/short-circuiting is the best option.

With some additional work, we can also fix some current oddities in Arrow.
If we redefine the exception we use to `shift` or `short-circuit` to the following.
We can have a `ControlThrowable` that uses the `cause.stacktrace` as the stacktrace,
and holds a `CancellationException` with the stacktrace.

```kotlin
private class ShiftCancellationException(
  val token: Token,
  val value: Any?,
  override val cause: CancellationException = CancellationException()
) : ControlThrowable("Shifted Continuation", cause) 
```

That way we can solve the issue we currently have with `bracketCase` that it signals,
`ExitCase.Failure` when a `ShortCircuit` has occurred (Arrow 1.0 computation runtime).

Since `bracketCase` can be aware of our `ShiftCancellationException`,
then it can take the `cause` to signal `ExitCase.Cancelled`.

Downsides:
  - With `CancellationException` we don't have to impose the rule **never catch ControlThrowable**.

### Open ends..

##### Does this break any semantics? 

Ideally it seems that `shift` should not be callable from `launch`, but afaik that is not possible in vanilla Kotlin.
With `CanellationException`, `shift` from within `async` seems to follow the Structured Concurrency Spec, when you don't call `await` it cannot cancel/shift its surrounding scope.
But in that fashion `shift` should also ignore `shift` from within its calls.

We could consider `launch`, `async`, and `coroutineScope` low-level operators where people need to keep the above restrictions in mind.

Since Arrow Fx Coroutines offers high-level operators which don't expose any of the issues above. I.e.
```kotlin
cont<String, List<Int>> {
  (0..100).parTraverse { i ->
    shift<Int>("error")
  }
}

cont<String, Int> {
  parZip({ shift<Int>("error") }, { 1 }) { a, b -> a + b }
}
```
