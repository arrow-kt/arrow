// This file was automatically generated from CircuitBreaker.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleCircuitbreaker01

import arrow.core.Either
import arrow.fx.coroutines.CircuitBreaker
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay

@ExperimentalTime
suspend fun main(): Unit {
  val circuitBreaker = CircuitBreaker.of(
    maxFailures = 2,
    resetTimeout = 2.seconds,
    exponentialBackoffFactor = 1.2,
    maxResetTimeout = 60.seconds,
  )
  circuitBreaker.protectOrThrow { "I am in Closed: ${circuitBreaker.state()}" }.also(::println)

  println("Service getting overloaded . . .")

  Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
  Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
  circuitBreaker.protectEither { }.also { println("I am Open and short-circuit with ${it}. ${circuitBreaker.state()}") }

  println("Service recovering . . .").also { delay(2000) }

  circuitBreaker.protectOrThrow { "I am running test-request in HalfOpen: ${circuitBreaker.state()}" }.also(::println)
  println("I am back to normal state closed ${circuitBreaker.state()}")
}
