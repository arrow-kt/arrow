// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl01

import arrow.core.raise.Raise
import arrow.core.raise.recover

fun Raise<String>.failure(): Int = raise("failed")

fun Raise<Nothing>.recovered(): Int =
  recover({ failure() }) { _: String -> 1 }
