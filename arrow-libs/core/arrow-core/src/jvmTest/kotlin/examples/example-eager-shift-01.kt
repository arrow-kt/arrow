// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift01

import arrow.core.continuations.EagerShift
import arrow.core.continuations.toEither

val effect: suspend EagerShift<String>.() -> Int = {
  val x = shift<Int>("error")
  1
}
val res = effect.toEither() // Either.Left("error")
