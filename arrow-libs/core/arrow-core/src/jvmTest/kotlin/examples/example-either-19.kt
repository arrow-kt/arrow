// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither19

import arrow.core.left
import arrow.core.getOrElse

val x = "hello".left()
val getOr7 = x.getOrElse { 7 }
fun main() {
 println("getOr7 = $getOr7")
}
