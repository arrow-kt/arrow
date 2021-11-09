// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.exampleCont02

import arrow.cont
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
  cont<String, Int> {
    shift("Hello, World!")
  }.fold({ str: String -> str }, { int -> int.toString() })
   .let(::println)

  cont<String, Int> {
    1000
  }.fold({ str: String -> str.length }, { int -> int })
   .let(::println)
}
