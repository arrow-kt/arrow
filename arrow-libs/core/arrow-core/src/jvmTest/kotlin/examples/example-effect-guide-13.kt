// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide13

import arrow.core.continuations.effect

suspend fun main() {

  effect<String, suspend () -> Unit> {
    suspend { shift("error") }
  }.fold({ }, { leakedShift -> leakedShift.invoke() })
}
