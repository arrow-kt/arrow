// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions02

import arrow.fx.coroutines.resourceScope
import arrow.fx.coroutines.closeable
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  resourceScope {
    val a: FileInputStream = closeable { FileInputStream(src) }
    val b: FileInputStream = closeable { FileInputStream(dest) }
    /** read from `a` and write to `b`. **/
  } // Both resources will be closed accordingly to their #close methods
