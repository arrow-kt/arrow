// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption13

import arrow.*
import arrow.core.*
import arrow.core.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

import arrow.core.toOption

val nullString: String? = null
val valueFromNull = nullString.toOption()

val helloString: String? = "Hello"
val valueFromStr = helloString.toOption()
fun main () {
 println("valueFromNull = $valueFromNull")
 println("valueFromStr = $valueFromStr")
}
