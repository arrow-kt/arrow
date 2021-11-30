// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor13

import arrow.core.Ior

fun main() {
  Ior.Both(5, 12).exists { it > 10 } // Result: true
  Ior.Right(12).exists { it > 10 }   // Result: true
  Ior.Right(7).exists { it > 10 }    // Result: false
  val left: Ior<Int, Int> = Ior.Left(12)
  left.exists { it > 10 }      // Result: false
}
