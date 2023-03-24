// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl04

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.core.recover
import arrow.core.right
import io.kotest.matchers.shouldBe

fun Raise<String>.failure(): Int = raise("failed")

fun test() {
  val failure: Either<String, Int> = either { failure() }

  failure.recover { _: String -> 1.right().bind() } shouldBe Either.Right(1)

  failure.recover { msg: String -> raise(msg.toList()) } shouldBe Either.Left(listOf('f', 'a', 'i', 'l', 'e', 'd'))

  recover({ failure.bind() }) { 1 } shouldBe failure.getOrElse { 1 }
}
