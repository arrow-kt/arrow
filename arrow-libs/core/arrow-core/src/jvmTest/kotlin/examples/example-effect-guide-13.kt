// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide13

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun main() {

  effect<String, suspend () -> Unit> {
    suspend { shift("error") }
  }.fold({ }, { leakedShift -> leakedShift.invoke() })

  val leakedAsync = coroutineScope<suspend () -> Deferred<Unit>> {
    suspend {
      async {
        println("I am never going to run, until I get called invoked from outside")
      }
    }
  }

  leakedAsync.invoke().await()
}
