// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect04

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.recover
import arrow.core.continuations.catch

val failed: Effect<String, Int> =
  effect { shift("failed") }

val default: Effect<Nothing, Int> =
  failed.recover { -1 }

val resolved: Effect<Nothing, Int> =
  failed.recover { it.length }

val default2: Effect<Double, Int> = default
val resolved2: Effect<Unit, Int> = resolved

val newError: Effect<List<Char>, Int> =
  failed.recover { str ->
    shift(str.reversed().toList())
  }

val newException: Effect<Nothing, Int> =
  failed.recover { str -> throw RuntimeException(str) }

val foreign = effect<String, Int> {
  throw RuntimeException("BOOM!")
}

val default3: Effect<String, Int> =
  foreign.catch { -1 }

val resolved3: Effect<String, Int> =
  foreign.catch { it.message?.length ?: -1 }

val default4: Effect<Nothing, Int> =
  foreign
    .recover<String, Nothing, Int> { -1 }
    .catch { -2 }

val default5: Effect<String, Int> =
  foreign
    .catch { ex: RuntimeException -> -1 }
    .catch { ex: java.sql.SQLException -> -2 }

suspend fun java.sql.SQLException.isForeignKeyViolation(): Boolean = true

val rethrown: Effect<String, Int> =
  failed.catch { ex: java.sql.SQLException ->
    if(ex.isForeignKeyViolation()) shift("foreign key violation")
    else throw ex
  }
