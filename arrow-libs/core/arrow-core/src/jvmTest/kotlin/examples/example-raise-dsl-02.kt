// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl02

import arrow.core.getOrElse
import arrow.core.left
import arrow.core.raise.Raise
import arrow.core.raise.effect
import arrow.core.raise.recover
import arrow.core.raise.getOrElse
import io.kotest.matchers.shouldBe

fun Raise<String>.failure(): Int = raise("failed")

fun Raise<List<Char>>.recovered(): Int =
  recover({ failure() }) { msg: String -> raise(msg.toList()) }

suspend fun Raise<List<Char>>.recovered2(): Int =
  effect { failure() } getOrElse { msg: String -> raise(msg.toList()) }

fun Raise<List<Char>>.recovered3(): Int =
  "failed".left() getOrElse { msg: String -> raise(msg.toList()) }

fun test(): Unit {
  recover({ "failed".left().bind() }) { 1 } shouldBe "failed".left().getOrElse { 1 }
}
