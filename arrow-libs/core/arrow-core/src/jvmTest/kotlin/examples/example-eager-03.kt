// This file was automatically generated from EagerEffect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEager03

import arrow.core.continuations.eagerEffect
import arrow.core.continuations.attempt

object User
object Error

val x = eagerEffect<Error, User> {
  throw IllegalArgumentException("builder missed args")
}.attempt<IllegalArgumentException, Error, User> { shift(Error) }

val y = eagerEffect<Nothing, User> {
  throw IllegalArgumentException("builder missed args")
}.attempt<IllegalArgumentException, Error, User> { shift(Error) }
