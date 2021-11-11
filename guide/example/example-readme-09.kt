// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme09

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

import java.io.*

fun <A> Resource<A>.releaseCase(releaseCase: suspend (A, ExitCase) -> Unit): Resource<A> =
  flatMap { a -> Resource({ a }, releaseCase) }

suspend fun test() = checkAll(Arb.string()) { error ->
  val exit = CompletableDeferred<ExitCase>()

  fun bufferedReader(path: String): Resource<BufferedReader> =
    Resource.fromAutoCloseable { File(path).bufferedReader() }
      .releaseCase { _, exitCase -> exit.complete(exitCase) }

  cont<String, Int> {
    val lineCount = bufferedReader("build.gradle.kts")
      .use { reader -> shift<Int>(error) }
    lineCount
  }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
