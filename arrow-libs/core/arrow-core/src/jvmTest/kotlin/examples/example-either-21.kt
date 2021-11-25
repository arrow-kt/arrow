// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither21

import arrow.core.left
import arrow.core.getOrHandle

val x = "hello".left()
val value = x.getOrHandle { "$it world!" }
fun main() {
 println("value = $value")
}
