//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[CircuitBreaker](index.md)

# CircuitBreaker

[common]\
class [CircuitBreaker](index.md)

A [CircuitBreaker](index.md) is used to protect resources or services from being overloaded When a service is being overloaded, interacting with it more will only worsen its overloaded state. Especially when combined with retry mechanisms such as [Schedule](../-schedule/index.md), in some cases simply using a back-off retry policy might not be sufficient during peak traffics.

To allow such overloaded resources from overloading, [CircuitBreaker](index.md) can help you protect the service by failing-fast. Thus [CircuitBreaker](index.md) helps us to achieve stability and prevent cascading failures in distributed systems.

[CircuitBreaker](index.md) has three [CircuitBreaker.State](-state/index.md):

<ol><li>[Closed](-state/-closed/index.md): This is its normal state, where requests are being made. The state in which [CircuitBreaker](index.md) starts.</li>

<ul><li>When an exception occurs it increments the failure counter</li><li>A successful request will reset the failure counter to zero</li><li>When the failure counter reaches the [maxFailures](../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/max-failures.md) threshold, the breaker is tripped into the [Open](-state/-open/index.md) state</li></ul>

<li>[Open](-state/-open/index.md): The [CircuitBreaker](index.md) will short-circuit/fail-fast all requests</li>

<ul><li>All requests short-circuit/fail-fast with ExecutionRejected</li><li>If a request is made after the configured [resetTimeout](../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/reset-timeout.md) passes, the [CircuitBreaker](index.md) is tripped into the a [HalfOpen](-state/-half-open/index.md) state, allowing one request to go through as a test.</li></ul>

<li>[HalfOpen](-state/-half-open/index.md): The [CircuitBreaker](index.md) is in this state while it's allowing a request to go through, as as a test request</li>

<ul><li>All other requests made while test request is still running will short-circuit/fail-fast.</li><li>If the test request succeeds then the [CircuitBreaker](index.md) is tripped back into [Closed](-state/-closed/index.md), with the resetTimeout and the failures count also reset to initial values.</li><li>If the test request fails, then the [CircuitBreaker](index.md) is tripped back into [Open](-state/-open/index.md), the [resetTimeout](../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/reset-timeout.md) is multiplied by the [exponentialBackoffFactor](../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/exponential-backoff-factor.md), up to the configured [maxResetTimeout](../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/max-reset-timeout.md).</li></ul>

</ol>

Let's say we'd want to create a [CircuitBreaker](index.md) that only allows us to call a remote service twice, and then whenever more than two requests fail with an exception, the circuit breaker starts short-circuiting failing-fast.

import arrow.core.Either\
import arrow.core.flatten\
import arrow.fx.coroutines.CircuitBreaker\
import kotlin.time.Duration\
import kotlin.time.ExperimentalTime\
import kotlinx.coroutines.delay\
\
@ExperimentalTime\
suspend fun main(): Unit {\
//sampleStart\
  val circuitBreaker = CircuitBreaker.of(\
    maxFailures = 2,\
    resetTimeout = Duration.seconds(2),\
    exponentialBackoffFactor = 1.2,\
    maxResetTimeout = Duration.seconds(60),\
  )\
  circuitBreaker.protectOrThrow { "I am in Closed: ${circuitBreaker.state()}" }.also(::println)\
\
  println("Service getting overloaded . . .")\
\
  Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)\
  Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)\
  circuitBreaker.protectEither { }.also { println("I am Open and short-circuit with ${it}. ${circuitBreaker.state()}") }\
\
  println("Service recovering . . .").also { delay(2000) }\
\
  circuitBreaker.protectOrThrow { "I am running test-request in HalfOpen: ${circuitBreaker.state()}" }.also(::println)\
  println("I am back to normal state closed ${circuitBreaker.state()}")\
//sampleEnd\
}<!--- KNIT example-circuitbreaker-01.kt -->

A common pattern to make fault-tolerant/resilient systems is to compose a [CircuitBreaker](index.md) with a backing-off policy retry Schedule to guarantee not overloading the resource and the client interacting with it. but also not the client that is interacting with the resource. Below you can see how the simple retry function will result in Either.Left&lt;CircuitBreaker.RejectedExecution&gt;, but when we combine it with another schedule, it will always call the CircuitBreaker on times that it could've entered the [HalfOpen](-state/-half-open/index.md) state. The reason why [Schedule](../-schedule/index.md) is not sufficient to make your system resilient is because you also have to take into account parallel calls to your functions, ; In contrast, a [CircuitBreaker](index.md) can track failures of every function call or even different functions to the same resource or service.

import arrow.core.Either\
import arrow.fx.coroutines.CircuitBreaker\
import arrow.fx.coroutines.Schedule\
import arrow.fx.coroutines.retry\
import kotlin.time.Duration.Companion.seconds\
import kotlin.time.ExperimentalTime\
import kotlinx.coroutines.delay\
\
@ExperimentalTime\
suspend fun main(): Unit {\
  suspend fun apiCall(): Unit {\
    println("apiCall . . .")\
    throw RuntimeException("Overloaded service")\
  }\
\
  //sampleStart\
  val circuitBreaker = CircuitBreaker.of(\
    maxFailures = 2,\
    resetTimeout = seconds(2),\
    exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor\
    maxResetTimeout = seconds(60), // limit exponential back-off time\
  )\
\
  suspend fun &lt;A&gt; resilient(schedule: Schedule&lt;Throwable, *&gt;, f: suspend () -&gt; A): A =\
    schedule.retry { circuitBreaker.protectOrThrow(f) }\
\
  Either.catch {\
    resilient(Schedule.recurs(5), ::apiCall)\
  }.let { println("recurs(5) apiCall twice and 4x short-circuit result from CircuitBreaker: $it") }\
\
  delay(2000)\
  println("CircuitBreaker ready to half-open")\
\
  // Retry once and when the CircuitBreaker opens after 2 failures then retry with exponential back-off with same time as CircuitBreaker's resetTimeout\
  val fiveTimesWithBackOff = Schedule.recurs&lt;Throwable&gt;(1) andThen\
    Schedule.exponential(seconds(2)) and Schedule.recurs(5)\
\
  Either.catch {\
    resilient(fiveTimesWithBackOff, ::apiCall)\
  }.let { println("exponential(seconds(2)) and recurs(5) always retries with actual apiCall: $it") }\
  //sampleEnd\
}<!--- KNIT example-circuitbreaker-02.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
| [ExecutionRejected](-execution-rejected/index.md) | [common]<br>class [ExecutionRejected](-execution-rejected/index.md)(reason: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), state: [CircuitBreaker.State](-state/index.md)) : [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [State](-state/index.md) | [common]<br>sealed class [State](-state/index.md)<br>The initial state when initializing a [CircuitBreaker](index.md) is [Closed](-state/-closed/index.md). |

## Functions

| Name | Summary |
|---|---|
| [awaitClose](await-close.md) | [common]<br>suspend fun [awaitClose](await-close.md)()<br>Awaits for this CircuitBreaker to be [CircuitBreaker.State.Closed](-state/-closed/index.md). |
| [doOnClosed](do-on-closed.md) | [common]<br>fun [doOnClosed](do-on-closed.md)(callback: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [CircuitBreaker](index.md)<br>Returns a new circuit breaker that wraps the state of the source and that will fire the given callback upon the circuit breaker transitioning to the CircuitBreaker.Closed state. |
| [doOnHalfOpen](do-on-half-open.md) | [common]<br>fun [doOnHalfOpen](do-on-half-open.md)(callback: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [CircuitBreaker](index.md)<br>Returns a new circuit breaker that wraps the state of the source and that will fire the given callback upon the circuit breaker transitioning to the CircuitBreaker.HalfOpen state. |
| [doOnOpen](do-on-open.md) | [common]<br>fun [doOnOpen](do-on-open.md)(callback: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [CircuitBreaker](index.md)<br>Returns a new circuit breaker that wraps the state of the source and that will fire the given callback upon the circuit breaker transitioning to the CircuitBreaker.Open state. |
| [doOnRejectedTask](do-on-rejected-task.md) | [common]<br>fun [doOnRejectedTask](do-on-rejected-task.md)(callback: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [CircuitBreaker](index.md)<br>Returns a new circuit breaker that wraps the state of the source and that upon a task being rejected will execute the given callback. |
| [protectEither](protect-either.md) | [common]<br>suspend fun &lt;[A](protect-either.md)&gt; [protectEither](protect-either.md)(fa: suspend () -&gt; [A](protect-either.md)): [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[CircuitBreaker.ExecutionRejected](-execution-rejected/index.md), [A](protect-either.md)&gt;<br>Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker. If an exception in [fa](protect-either.md) occurs, other than an [ExecutionRejected](-execution-rejected/index.md) exception, it will be rethrown. |
| [protectOrThrow](protect-or-throw.md) | [common]<br>suspend tailrec fun &lt;[A](protect-or-throw.md)&gt; [protectOrThrow](protect-or-throw.md)(fa: suspend () -&gt; [A](protect-or-throw.md)): [A](protect-or-throw.md)<br>Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker. If an exception in [fa](protect-or-throw.md) occurs it will be rethrown |
| [state](state.md) | [common]<br>suspend fun [state](state.md)(): [CircuitBreaker.State](-state/index.md)<br>Returns the current [CircuitBreaker.State](-state/index.md), meant for debugging purposes. |
