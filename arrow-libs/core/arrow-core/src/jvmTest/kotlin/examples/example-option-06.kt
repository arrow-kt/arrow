// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption06

import arrow.core.*

val myString: String? = "Nullable string"
val option: Option<String> = Option.fromNullable(myString)
fun main () {
 println("option = $option")
}
