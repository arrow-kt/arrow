// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption10

import arrow.core.Option
import arrow.core.Some

val fold =
 Some(3).fold({ 1 }, { it * 3 })
fun main () {
 println(fold)
}
