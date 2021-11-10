// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme02

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

import java.io.*

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

suspend fun test() {
  readFile("").toEither() shouldBe Either.Left(EmptyPath)
  readFile("not-found").toValidated() shouldBe  Validated.Invalid(FileNotFound("not-found"))
  readFile("gradle.properties").toIor() shouldBe Ior.Left(FileNotFound("gradle.properties"))
  readFile("not-found").toOption { None } shouldBe None
  readFile("nullable").fold({ _: FileError -> null }, { it }) shouldBe null
}
