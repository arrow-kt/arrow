// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions02

import arrow.fx.coroutines.*
import arrow.fx.coroutines.continuations.*
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  resource {
    val a: FileInputStream = closeable { FileInputStream(src) }
    val b: FileInputStream = closeable { FileInputStream(dest) }
    Pair(a, b)
  }.use { (a: FileInputStream, b: FileInputStream) ->
     /** read from [a] and write to [b]. **/
     // Both resources will be closed accordingly to their #close methods
  }
