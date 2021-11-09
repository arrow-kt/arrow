// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme08

import arrow.cont
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.bracketCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.fromAutoCloseable
import java.io.BufferedReader
import java.io.File

suspend fun bracketCase() = cont<String, Int> {
  bracketCase(
   acquire = { File("gradle.properties").bufferedReader() },
   use = { reader -> 
    // some logic
    shift("file doesn't contain right content")
   },
   release = { reader, exitCase -> 
     reader.close()
     println(exitCase) // ExitCase.Cancelled(ShiftCancellationException("Shifted Continuation"))
   }
  )
}.fold(::println, ::println) // "file doesn't contain right content"

// Available from Arrow 1.1.x
fun <A> Resource<A>.releaseCase(releaseCase: (A, ExitCase) -> Unit): Resource<A> =
  flatMap { a -> Resource({ a }, releaseCase) }

fun bufferedReader(path: String): Resource<BufferedReader> =
  Resource.fromAutoCloseable {
    File(path).bufferedReader()
  }.releaseCase { _, exitCase -> println(exitCase) }

suspend fun resource() = cont<String, Int> {
  bufferedReader("gradle.properties").use { reader ->
  // some logic
  shift("file doesn't contain right content")
 } // ExitCase.Cancelled(ShiftCancellationException("Shifted Continuation")) printed from release
}
