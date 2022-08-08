// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions03

import arrow.fx.coroutines.resource
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.use
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  resource {
    val a: FileInputStream = autoCloseable { FileInputStream(src) }
    val b: FileInputStream = autoCloseable { FileInputStream(dest) }
    Pair(a, b)
  }.use { (a: FileInputStream, b: FileInputStream) ->
     /** read from [a] and write to [b]. **/
     // Both resources will be closed accordingly to their #close methods
  }
