// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect05

import arrow.core.continuations.effect
import arrow.core.continuations.attempt

object User
object Error

val x = effect<Error, User> {
  throw IllegalArgumentException("builder missed args")
}.attempt<IllegalArgumentException, Error, User> { shift(Error) }

val y = effect<Nothing, User> {
  throw IllegalArgumentException("builder missed args")
}.attempt<IllegalArgumentException, Error, User> { shift(Error) }
