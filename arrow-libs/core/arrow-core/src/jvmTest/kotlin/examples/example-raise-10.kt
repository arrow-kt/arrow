// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise10

import arrow.core.raise.Effect
import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.core.raise.ensureNotNull
import arrow.core.raise.ensure
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import arrow.core.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

@JvmInline
value class Content(val body: List<String>)

sealed interface FileError
@JvmInline value class SecurityError(val msg: String?) : FileError
@JvmInline value class FileNotFound(val path: String) : FileError
object EmptyPath : FileError {
  override fun toString() = "EmptyPath"
}

fun readFile(path: String?): Effect<FileError, Content> = effect {
  ensureNotNull(path) { EmptyPath }
  ensure(path.isNotEmpty()) { EmptyPath }
  try {
    val lines = File(path).readLines()
    Content(lines)
  } catch (e: FileNotFoundException) {
    raise(FileNotFound(path))
  } catch (e: SecurityException) {
    raise(SecurityError(e.message))
  }
}

suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }

suspend fun main() {
  val exit = CompletableDeferred<ExitCase>()
  effect<FileError, Int> {
    withContext(Dispatchers.IO) {
      val job = launch { awaitExitCase(exit) }
      val content = readFile("failure").bind()
      job.join()
      content.body.size
    }
  }.fold({ e -> e shouldBe FileNotFound("failure") }, { fail("Int can never be the result") })
  exit.await().shouldBeInstanceOf<ExitCase>()
}
