// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme04

import arrow.Cont
import arrow.cont
import arrow.core.identity
import kotlinx.coroutines.runBlocking

val failed: Cont<String, Int> =
  cont { shift("failed") }

val resolved: Cont<Nothing, Int> =
  failed.handleError { it.length }

val newError: Cont<List<Char>, Int> =
  failed.handleErrorWith { str ->
    cont { shift(str.reversed().toList()) }
  }

val redeemed: Cont<Nothing, Int> =
  failed.redeem({ str -> str.length }, ::identity)

val captured: Cont<String, Result<Int>> = cont<String, Int> {
  throw RuntimeException("Boom")
}.attempt()

fun main() = runBlocking<Unit> {
  println(failed.toEither())
  println(resolved.toEither())
  println(newError.toEither())
  println(redeemed.toEither())
  println(captured.toEither())
}
