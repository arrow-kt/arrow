// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource09

import arrow.fx.coroutines.*
import arrow.fx.coroutines.ExitCase.Companion.ExitCase

val resource =
  resource({ "Acquire" }) { _, exitCase -> println("Release $exitCase") }

suspend fun main(): Unit {
  val (acquired: String, release: suspend (ExitCase) -> Unit) = resource.allocated()
  try {
    /** Do something with A */
    release(ExitCase.Completed)
  } catch(e: Throwable) {
     release(ExitCase(e))
  }
}
