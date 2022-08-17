// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect04

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import arrow.core.continuations.catch
import arrow.core.continuations.attempt

val failed: Effect<String, Int> =
  effect { shift("failed") }

val default: Effect<Nothing, Int> =
  failed.catch { -1 }

val resolved: Effect<Nothing, Int> =
  failed.catch { it.length }

val default2: Effect<Double, Int> = default
val resolved2: Effect<Unit, Int> = resolved

val newError: Effect<List<Char>, Int> =
  failed.catch { str ->
    shift(str.reversed().toList())
  }

val newException: Effect<Nothing, Int> =
  failed.catch { str -> throw RuntimeException(str) }

val foreign = effect<String, Int> {
  throw RuntimeException("BOOM!")
}

val default3: Effect<String, Int> =
  foreign.attempt { -1 }

val resolved3: Effect<String, Int> =
  foreign.attempt { it.message?.length ?: -1 }

val default4: Effect<Nothing, Int> =
  foreign
    .catch<String, Nothing, Int> { -1 }
    .attempt { -2 }

val default5: Effect<String, Int> =
  foreign
    .attempt { ex: RuntimeException -> -1 }
    .attempt { ex: java.sql.SQLException -> -2 }

suspend fun java.sql.SQLException.isForeignKeyViolation(): Boolean = true

val rethrown: Effect<String, Int> =
  failed.attempt { ex: java.sql.SQLException ->
    if(ex.isForeignKeyViolation()) shift("foreign key violation")
    else throw ex
  }
