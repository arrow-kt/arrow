// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide01

import arrow.core.continuations.effect
import arrow.core.continuations.fold

suspend fun main() {

  effect<String, suspend () -> Unit> {
    suspend { raise("error") }
  }.fold({ }, { leakedRaise -> leakedRaise.invoke() })
}
