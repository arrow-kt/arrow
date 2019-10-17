package arrow.aql

import arrow.core.ForId
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.core.Option
import arrow.core.Some
import arrow.core.None
import arrow.typeclasses.Foldable
import arrow.typeclasses.Order

interface Min<F> {

  fun foldable(): Foldable<F>

  fun <A, Y, Z> Query<F, A, Y>.min(ord: Order<Z>, f: A.() -> Z): Query<ForId, Option<Z>, Option<Z>> =
    Query(
      select = ::identity,
      from = Id(foldable().run {
        from.foldLeft(None) { acc: Option<Z>, a: A ->
          acc.fold({ Some(f(a)) },
            { Some(if (ord.run { it < f(a) }) it else f(a)) })
        }
      }))

  fun <Z> Query<ForId, Option<Z>, Option<Z>>.value(): Option<Z> =
    foldable().run {
      this@value.from.value()
    }
}
