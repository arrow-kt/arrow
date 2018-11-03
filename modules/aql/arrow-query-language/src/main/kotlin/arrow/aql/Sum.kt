package arrow.aql

import arrow.core.*
import arrow.data.ForListK
import arrow.data.fix
import arrow.data.k
import arrow.typeclasses.Foldable

interface Sum<F> {

  fun foldable(): Foldable<F>

  infix fun <A, Z> Query<F, A, Z>.sum(f: (A) -> Long): Query<ForListK, Long, Long> =
    foldable().run {
        Query(
          select = ::identity,
          from = listOf(from.foldLeft(0L) { acc, a ->
            acc + f(a)
          }).k()
        )
    }

  fun Query<ForListK, Long, Long>.value(): Long =
    foldable().run {
      this@value.from.fix().firstOrNone().getOrElse { 0L }
    }

}