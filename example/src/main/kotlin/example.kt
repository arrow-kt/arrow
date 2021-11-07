import arrow.core.None
import java.io.File
import java.io.FileNotFoundException

sealed class FileError
data class SecurityError(val msg: String?) : FileError()
data class FileNotFound(val path: String): FileError()
object EmptyPath: FileError()
data class Content(val body: List<String>)

fun readFile(path: String): Cont<FileError, Content> = cont {
  ensure(path.isNotEmpty()) { EmptyPath }
  try {
    Content(File(path).readLines())
  } catch (e: FileNotFoundException) {
    shift(FileNotFound(path))
  } catch (e: SecurityException) {
    shift(SecurityError(e.message))
  }
}

suspend fun main() {
  readFile("").toEither().let(::println)
  readFile("not-found").toValidated().let(::println)
  readFile("gradle.properties").toIor().let(::println)
  readFile("not-found").toOption { None } .let(::println)
  readFile("nullable").fold({ _: FileError -> null }, { it }).let(::println)
}
