// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor13

import arrow.core.Ior

fun main() {
  val right: Ior<Int, Int> = Ior.Right(12)
  right.isLeft { it > 10 }   // Result: false
  Ior.Both(12, 7).isLeft { it > 10 }    // Result: false
  Ior.Left(12).isLeft { it > 10 }      // Result: true
}
