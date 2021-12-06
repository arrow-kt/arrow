// This file was automatically generated from Option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOption22

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

import arrow.core.Some
import arrow.core.None
import arrow.core.Option

fun main() {
  Some(12).exists { it > 10 } // Result: 12
  Some(7).exists { it > 10 }  // Result: null

  val none: Option<Int> = None
  none.exists { it > 10 }      // Result: null
}
