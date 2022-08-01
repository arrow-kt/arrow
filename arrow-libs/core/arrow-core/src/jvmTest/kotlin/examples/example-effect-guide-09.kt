// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide09

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.fromAutoCloseable
import arrow.fx.coroutines.releaseCase
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import java.io.BufferedReader
import java.io.File

suspend fun main() {
  val error = "Error"
  val exit = CompletableDeferred<ExitCase>()

  fun bufferedReader(path: String): Resource<BufferedReader> =
    Resource.fromAutoCloseable { File(path).bufferedReader() }
      .releaseCase { _, exitCase -> exit.complete(exitCase) }

  effect<String, Int> {
    val lineCount = bufferedReader("build.gradle.kts")
      .use { reader -> shift<Int>(error) }
    lineCount
  }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
