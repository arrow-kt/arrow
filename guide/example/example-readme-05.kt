// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme05

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*

suspend fun parZip(): Unit = cont<String, Int> {
  parZip({
   delay(1_000_000) // Cancelled by shift 
  }, { shift<Int>("error") }) { _, int -> int }
}.fold(::println, ::println) // "error"
