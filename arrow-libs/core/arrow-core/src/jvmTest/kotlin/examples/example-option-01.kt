// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption01

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

import arrow.core.Option
import arrow.core.Some
import arrow.core.none

val someValue: Option<String> = Some("I am wrapped in something")
val emptyValue: Option<String> = none()
fun main() {
 println("value = $someValue")
 println("emptyValue = $emptyValue")
}
