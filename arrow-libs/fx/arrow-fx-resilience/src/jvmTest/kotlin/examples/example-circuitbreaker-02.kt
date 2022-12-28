// This file was automatically generated from CircuitBreaker.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleCircuitbreaker02

import arrow.core.Either
import arrow.fx.resilience.CircuitBreaker
import arrow.fx.resilience.Schedule
import arrow.fx.resilience.retry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay

@ExperimentalTime
suspend fun main(): Unit {
  suspend fun apiCall(): Unit {
    println("apiCall . . .")
    throw RuntimeException("Overloaded service")
  }

  //sampleStart
  val circuitBreaker = CircuitBreaker.of(
    maxFailures = 2,
    resetTimeout = seconds(2),
    exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
    maxResetTimeout = seconds(60), // limit exponential back-off time
  )

  suspend fun <A> resilient(schedule: Schedule<Throwable, *>, f: suspend () -> A): A =
    schedule.retry { circuitBreaker.protectOrThrow(f) }

  Either.catch {
    resilient(Schedule.recurs(5), ::apiCall)
  }.let { println("recurs(5) apiCall twice and 4x short-circuit result from CircuitBreaker: $it") }

  delay(2000)
  println("CircuitBreaker ready to half-open")

  // Retry once and when the CircuitBreaker opens after 2 failures then retry with exponential back-off with same time as CircuitBreaker's resetTimeout
  val fiveTimesWithBackOff = Schedule.recurs<Throwable>(1) andThen
    Schedule.exponential(seconds(2)) and Schedule.recurs(5)

  Either.catch {
    resilient(fiveTimesWithBackOff, ::apiCall)
  }.let { println("exponential(seconds(2)) and recurs(5) always retries with actual apiCall: $it") }
  //sampleEnd
}
