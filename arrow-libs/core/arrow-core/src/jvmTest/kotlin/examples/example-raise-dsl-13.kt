// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl13

import arrow.core.getOrElse
import arrow.core.raise.attempt
import arrow.core.raise.either
import io.kotest.matchers.shouldBe
import kotlin.random.Random

val foo = either { if (Random.nextBoolean()) raise("failed") else 42 }

fun test() {
  either {
    val msg = attempt { return@either foo.bind() }
    raise(msg.toList())
  } shouldBe foo.mapLeft(String::toList)

  run {
    attempt { return@run foo.bind() }
    1
  } shouldBe foo.getOrElse { 1 }
}

