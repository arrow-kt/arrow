// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleArrowCoreEitherContains01

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

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.contains

fun main() {
  Right("something").contains("something") // Result: true
  Right("something").contains("anything")  // Result: false
  Left("something").contains("something")  // Result: false
}
