// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither17

import arrow.core.left
import arrow.core.getOrElse

val x = "hello".left()
val value = x.getOrElse { "$it world!" }
fun main() {
 println("value = $value")
}
