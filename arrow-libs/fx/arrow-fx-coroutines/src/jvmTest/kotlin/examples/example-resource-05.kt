// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource05

import arrow.fx.coroutines.*

val resource = resource(
  { 42.also { println("Getting expensive resource") } },
  { r, exitCase -> println("Releasing expensive resource: $r, exit: $exitCase") }
)

suspend fun main(): Unit =
  resource.use { println("Expensive resource under use! $it") }
