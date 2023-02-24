// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption05

import arrow.core.*

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

 //sampleStart
val valueSome = maybeItWillReturnSomething(true).isEmpty()
val valueNone = maybeItWillReturnSomething(false).isEmpty()
fun main() {
 println("valueSome = $valueSome")
 println("valueNone = $valueNone")
}
