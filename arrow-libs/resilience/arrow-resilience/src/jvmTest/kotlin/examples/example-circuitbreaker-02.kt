// This file was automatically generated from CircuitBreaker.kt by Knit tool. Do not edit.
package arrow.resilience.examples.exampleCircuitbreaker02

import arrow.core.Either
import arrow.resilience.CircuitBreaker
import arrow.resilience.Schedule
import arrow.resilience.retry
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun main(): Unit {
  suspend fun apiCall(): Unit {
    println("apiCall . . .")
    throw RuntimeException("Overloaded service")
  }

  //sampleStart
  val circuitBreaker = CircuitBreaker(
    resetTimeout = 2.seconds,
    openingStrategy = CircuitBreaker.OpeningStrategy.Count(maxFailures = 2),
    exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
    maxResetTimeout = 60.seconds, // limit exponential back-off time
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
    Schedule.exponential(2.seconds) and Schedule.recurs(5)

  Either.catch {
    resilient(fiveTimesWithBackOff, ::apiCall)
  }.let { println("exponential(2.seconds) and recurs(5) always retries with actual apiCall: $it") }
  //sampleEnd
}
