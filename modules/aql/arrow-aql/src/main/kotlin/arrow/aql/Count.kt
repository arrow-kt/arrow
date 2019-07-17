package arrow.aql

import arrow.core.extensions.monoid
import arrow.core.firstOrNone
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.ForListK
import arrow.core.fix
import arrow.core.k
import arrow.typeclasses.Foldable

interface Count<F> {

  fun foldable(): Foldable<F>

  fun <A, Z> Query<F, A, Z>.count(): Query<ForListK, Long, Long> =
    foldable().run {
      Query(
        select = ::identity,
        from = listOf(from.size(Long.monoid())).k()
      )
    }

  fun Query<ForListK, Long, Long>.value(): Long =
    this@value.from.fix().firstOrNone().getOrElse { 0L }
}
