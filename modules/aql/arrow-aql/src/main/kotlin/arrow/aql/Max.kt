package arrow.aql

import arrow.core.ForId
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.typeclasses.Foldable
import kotlin.math.max

interface Max<F> {

  fun foldable(): Foldable<F>

  infix fun <A, Z> Query<F, A, Z>.max(f: A.() -> Long): Query<ForId, Long, Long> =
    foldable().run {
      Query(
        select = ::identity,
        from = Id(from.foldLeft(Long.MIN_VALUE) { acc, a -> max(f(a), acc) })
      )
    }

  fun Query<ForId, Long, Long>.value(): Long =
    foldable().run {
      this@value.from.value()
    }
}
