// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise08

import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.bracketCase
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import arrow.core.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import java.io.BufferedReader
import java.io.File

suspend fun main() {
  val error = "Error"
  val exit = CompletableDeferred<ExitCase>()
  effect<String, Int> {
    bracketCase(
      acquire = { File("build.gradle.kts").bufferedReader() },
      use = { _: BufferedReader -> raise(error) },
      release = { reader, exitCase ->
        reader.close()
        exit.complete(exitCase)
      }
    )
  }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
