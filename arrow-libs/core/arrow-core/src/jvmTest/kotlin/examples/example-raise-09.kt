// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise09

import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.resourceScope
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import java.io.BufferedReader
import java.io.File

suspend fun main() {
  val error = "Error"
  val exit = CompletableDeferred<ExitCase>()

  suspend fun ResourceScope.bufferedReader(path: String): BufferedReader =
    autoCloseable { File(path).bufferedReader() }.also {
      onRelease { exitCase -> exit.complete(exitCase) }
    }

  resourceScope {
    effect<String, Int> {
      val reader = bufferedReader("build.gradle.kts")
      raise(error)
      reader.lineSequence().count()
    }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  }
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
