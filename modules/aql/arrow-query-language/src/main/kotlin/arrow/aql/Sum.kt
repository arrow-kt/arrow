package arrow.aql

import arrow.core.*
import arrow.typeclasses.Foldable

interface Sum<F> {

  fun foldable(): Foldable<F>

  infix fun <A, Z> Query<F, A, Z>.sum(f: A.() -> Long): Query<ForId, Long, Long> =
    foldable().run {
        Query(
          select = ::identity,
          from = Id(from.foldLeft(0L) { acc, a ->
            acc + f(a)
          })
        )
    }

  fun Query<ForId, Long, Long>.value(): Long =
    foldable().run {
      this@value.from.value()
    }

}