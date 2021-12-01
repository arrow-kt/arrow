// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions02

import arrow.fx.coroutines.*
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  Resource.fromCloseable { FileInputStream(src) }
    .zip(Resource.fromCloseable { FileInputStream(dest) })
    .use { (a: FileInputStream, b: FileInputStream) ->
       /** read from [a] and write to [b]. **/
       // Both resources will be closed accordingly to their #close methods
    }
