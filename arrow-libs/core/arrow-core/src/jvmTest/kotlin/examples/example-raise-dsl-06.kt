// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl06

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.recover
import arrow.core.raise.either
import kotlinx.coroutines.delay
import io.kotest.matchers.shouldBe

suspend fun test() {
  val empty: Option<Int> = None
  either {
    val x: Int = empty.bind { _: None -> 1 }
    val y: Int = empty.bind { _: None -> raise("Something bad happened: Boom!") }
    val z: Int = empty.recover { _: None ->
      delay(10)
      1
    }.bind { raise("Something bad happened: Boom!") }
    x + y + z
  } shouldBe Either.Left("Something bad happened: Boom!")
}
