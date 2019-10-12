package arrow.aql

import arrow.core.ForId
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.typeclasses.Foldable
import kotlin.math.min

interface Min<F> {

  fun foldable(): Foldable<F>

  infix fun <A, Z> Query<F, A, Z>.min(f: A.() -> Long): Query<ForId, Long, Long> =
    foldable().run {
      Query(
        select = ::identity,
        from = Id(from.foldLeft(Long.MAX_VALUE) { acc, a -> min(f(a), acc) })
      )
    }

  fun Query<ForId, Long, Long>.value(): Long =
    foldable().run {
      this@value.from.value()
    }
}
