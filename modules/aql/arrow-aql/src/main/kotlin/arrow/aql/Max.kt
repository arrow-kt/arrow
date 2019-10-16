package arrow.aql

import arrow.core.ForId
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.core.None
import arrow.core.Some
import arrow.core.Option
import arrow.typeclasses.Foldable
import arrow.typeclasses.Order

interface Max<F> {

  fun foldable(): Foldable<F>

  fun <A, X, Z> Query<F, A, Z>.max(ord: Order<X>, f: A.() -> X): Query<ForId, Option<X>, Option<X>> =
    Query(
      select = ::identity,
      from = Id(foldable().run {
        from.foldLeft(None) { acc: Option<X>, a: A ->
          acc.fold({ Some(f(a)) },
            { Some(if (ord.run { it > (f(a)) }) it else f(a)) })
        }
      }))

  fun Query<ForId, Long, Long>.value(): Long =
    foldable().run {
      this@value.from.value()
    }
}
