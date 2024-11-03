// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource10

import arrow.fx.coroutines.resourceScope
import arrow.fx.coroutines.autoCloseable
import java.io.FileInputStream

suspend fun copyFile(src: String, dest: String): Unit =
  resourceScope {
    val a: FileInputStream = autoCloseable { FileInputStream(src) }
    val b: FileInputStream = autoCloseable { FileInputStream(dest) }
    /** read from [a] and write to [b]. **/
  } // Both resources will be closed accordingly to their #close methods
