// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource09

import arrow.fx.coroutines.*
import arrow.fx.coroutines.ExitCase.Companion.ExitCase

val resource = Resource({ println("Acquire") }) { _, exitCase ->
 println("Release $exitCase")
}

suspend fun main(): Unit {
  val (acquire, release) = resource.allocated()
  val a = acquire()
  try {
    /** Do something with A */
    release(a, ExitCase.Completed)
  } catch(e: Throwable) {
     val e2 = runCatching { release(a, ExitCase(e)) }.exceptionOrNull()
     throw Platform.composeErrors(e, e2)
  }
}
