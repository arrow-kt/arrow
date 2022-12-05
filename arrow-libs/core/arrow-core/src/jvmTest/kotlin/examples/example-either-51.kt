// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither51

import arrow.core.*

suspend fun main(): Unit {
  //sampleStart
  Either.Right(7).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })
    .let(::println) // Either.Left(Value '7' is not equal to 10")

  Either.Right(10).filterOrOther({ it == 10 }, { "Value '$it' is not equal to 10" })
    .let(::println) // Either.Right(10)

  Either.Left(12).filterOrOther({ str: String -> str.contains("impossible") }, { -1 })
    .let(::println) // Either.Left(12)
  //sampleEnd
}
