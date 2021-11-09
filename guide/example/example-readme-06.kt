// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme06

import arrow.cont
import arrow.fx.coroutines.onCancel
import arrow.fx.coroutines.parTraverse
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
  cont<String, List<Unit>> {
    (1..5).parTraverse { index ->
      if (index == 5) shift("error")
      else onCancel({ delay(1_000_000) }) { println("I got cancelled") }
    }
  }.fold(::println, ::println)
}
