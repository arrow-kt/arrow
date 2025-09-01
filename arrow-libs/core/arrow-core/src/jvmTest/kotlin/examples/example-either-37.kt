// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither37

import arrow.core.Either
import arrow.core.catch
import arrow.core.shouldThrow
import io.kotest.matchers.shouldBe

fun test() {
  val left: Either<Throwable, Int> = Either.catch { throw RuntimeException("Boom!") }

  val caught: Either<Nothing, Int> = left.catch { _: RuntimeException -> 1 }
  val failure: Either<String, Int> = left.catch { _: RuntimeException -> raise("failure") }

  shouldThrow<RuntimeException> {
    val caught2: Either<Nothing, Int> = left.catch { _: IllegalStateException -> 1 }
    Unit
  }

  caught shouldBe Either.Right(1)
  failure shouldBe Either.Left("failure")
}
