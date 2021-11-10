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
