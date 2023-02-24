// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption03

import arrow.core.*

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

val value1 =
 maybeItWillReturnSomething(true)
    .getOrElse { "No value" }
fun main() {
 println(value1)
}
