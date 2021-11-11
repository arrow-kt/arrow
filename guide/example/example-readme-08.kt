// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme08

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

suspend fun test() = checkAll(Arb.string()) { error ->
  val exit = CompletableDeferred<ExitCase>()
  cont<String, Int> {
    bracketCase(
      acquire = { File("build.gradle.kts").bufferedReader() },
      use = { reader: BufferedReader -> shift(error) },
      release = { reader, exitCase ->
        reader.close()
        exit.complete(exitCase)
      }
    )
  }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
