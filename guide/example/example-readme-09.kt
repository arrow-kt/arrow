// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme09

import arrow.cont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import arrow.Cont
import arrow.ensureNotNull
import java.io.File
import java.io.FileNotFoundException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

@JvmInline
value class Content(val body: List<String>)

sealed interface FileError
@JvmInline value class SecurityError(val msg: String?) : FileError
@JvmInline value class FileNotFound(val path: String) : FileError
object EmptyPath : FileError {
  override fun toString() = "EmptyPath"
}

fun readFile(path: String?): Cont<FileError, Content> = cont {
  ensureNotNull(path) { EmptyPath }
  ensure(path.isNotEmpty()) { EmptyPath }
  try {
    val lines = File(path).readLines()
    Content(lines)
  } catch (e: FileNotFoundException) {
    shift(FileNotFound(path))
  } catch (e: SecurityException) {
    shift(SecurityError(e.message))
  }
}

fun <A: Job> A.onCancel(f: (CancellationException) -> Unit): A = also {
  invokeOnCompletion { error ->
    if (error is CancellationException) f(error) else Unit
  }
}

fun main() = runBlocking<Unit> {
  cont<FileError, Int> {
    withContext(Dispatchers.IO) {
      launch { delay(1_000_000) }.onCancel { println("Cancelled due to shift: $it") }
      val sleeper = async { delay(1_000_000) }.onCancel { println("Cancelled due to shift: $it") }
      val content = readFile("failure").bind()
      sleeper.await()
      content.body.size
    }
  }.fold(::println, ::println)
}
