// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect04

import arrow.core.continuations.effect

object User
object Error

val x = effect<Error, User> {
  throw IllegalArgumentException("builder missed args")
}.attempt { _: IllegalArgumentException -> shift(Error) }
