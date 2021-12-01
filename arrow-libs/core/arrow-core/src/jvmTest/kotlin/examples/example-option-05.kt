// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption05

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

 //sampleStart
val valueSome = maybeItWillReturnSomething(true) is None
val valueNone = maybeItWillReturnSomething(false) is None
fun main() {
 println("valueSome = $valueSome")
 println("valueNone = $valueNone")
}
