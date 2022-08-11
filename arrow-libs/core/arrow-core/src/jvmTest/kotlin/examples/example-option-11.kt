// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption11

import arrow.core.Option
import arrow.core.none

val fold =
 none<Int>().fold({ 1 }, { it * 3 })
fun main () {
 println(fold)
}
