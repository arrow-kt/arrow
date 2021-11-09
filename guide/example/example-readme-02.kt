// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme02

import arrow.Cont
import arrow.cont
import arrow.ensureNotNull
import arrow.core.None
import java.io.File
import java.io.FileNotFoundException
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

fun main() = runBlocking<Unit> {
  readFile("").toEither().also(::println)
  readFile("not-found").toValidated().also(::println) 
  readFile("gradle.properties").toIor().also(::println)
  readFile("not-found").toOption { None }.also(::println)
  readFile("nullable").fold({ _: FileError -> null }, { it }).also(::println)
}
