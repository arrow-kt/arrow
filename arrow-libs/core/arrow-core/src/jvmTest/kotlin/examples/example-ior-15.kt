// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor15

import arrow.core.Ior

fun main() {
  Ior.Right(12).isRight { it > 10 }   // Result: false
  Ior.Both(12, 7).isRight { it > 10 }    // Result: false
  val left: Ior<Int, Int> = Ior.Left(12)
  left.isRight { it > 10 }      // Result: true
}
