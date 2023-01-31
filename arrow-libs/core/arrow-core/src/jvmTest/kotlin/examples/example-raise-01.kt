// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise01

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.ensureNotNull
import arrow.core.raise.ensure

object EmptyPath

fun readFile(path: String): Effect<EmptyPath, Unit> = effect {
  if (path.isEmpty()) raise(EmptyPath) else Unit
}

fun readFile2(path: String?): Effect<EmptyPath, Unit> = effect {
  ensureNotNull(path) { EmptyPath }
  ensure(path.isNotEmpty()) { EmptyPath }
}
