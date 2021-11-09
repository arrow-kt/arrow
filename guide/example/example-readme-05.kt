// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme05

import arrow.cont
import arrow.fx.coroutines.parZip
import kotlinx.coroutines.delay

suspend fun parZip(): Unit = cont<String, Int> {
  parZip({
   delay(1_000_000) // Cancelled by shift 
  }, { shift<Int>("error") }) { _, int -> int }
}.fold(::println, ::println) // "error"
