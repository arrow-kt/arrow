// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption14

import arrow.core.firstOrNone
import arrow.core.toOption
import arrow.core.Option

val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")

val empty = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
val filled = Option.fromNullable(foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() })

fun main() {
 println("empty = $empty")
 println("filled = $filled")
}
