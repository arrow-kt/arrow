// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme10

import arrow.cont
import arrow.core.identity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import io.kotest.matchers.collections.shouldBeIn

fun main() = runBlocking {
  coroutineScope {
    cont<Int, String> {
      val fa = async<String> { shift(1) }
      val fb = async<String> { shift(2) }
      fa.await() + fb.await()
    }.fold(::identity, ::identity) shouldBeIn listOf(1, 2)
  }
}
