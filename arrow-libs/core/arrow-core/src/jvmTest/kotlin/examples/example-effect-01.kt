// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect01

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.ensureNotNull

object EmptyPath

fun readFile(path: String): Effect<EmptyPath, Unit> = effect {
  if (path.isEmpty()) shift(EmptyPath) else Unit
}

fun readFile2(path: String?): Effect<EmptyPath, Unit> = effect {
  ensureNotNull(path) { EmptyPath }
  ensure(path.isNotEmpty()) { EmptyPath }
}
