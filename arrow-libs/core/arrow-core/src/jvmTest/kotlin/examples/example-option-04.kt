// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption04

import arrow.core.*

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

val value2 =
 maybeItWillReturnSomething(false)
  .getOrElse { "No value" }
fun main() {
 println(value2)
}
