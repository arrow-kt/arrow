// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError03

import arrow.core.raise.effect
import arrow.core.raise.catch

object User
object Error

val x = effect<Error, User> {
  throw IllegalArgumentException("builder missed args")
}.catch { raise(Error) }

val y = effect<Nothing, User> {
  throw IllegalArgumentException("builder missed args")
}.catch<IllegalArgumentException, Error, User> { raise(Error) }
