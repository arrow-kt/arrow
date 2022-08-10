// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions03

import arrow.fx.coroutines.resourceScope
import arrow.fx.coroutines.autoCloseable
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  resourceScope {
    val a: FileInputStream = autoCloseable { FileInputStream(src) }
    val b: FileInputStream = autoCloseable { FileInputStream(dest) }
    /** read from [a] and write to [b]. **/
  } // Both resources will be closed accordingly to their #close methods
