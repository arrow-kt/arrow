// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor05

import arrow.core.Ior

fun main() {
  Ior.Right(12).bimap ({ "flower" }, { 12 }) // Result: Right(12)
  Ior.Left(12).bimap({ "flower" }, { 12 })  // Result: Left("flower")
  Ior.Both(12, "power").bimap ({ it * 2 }, { b -> "flower $b" })   // Result: Both("flower power", 24)
}
