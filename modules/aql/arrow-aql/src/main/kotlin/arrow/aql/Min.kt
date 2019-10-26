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

  fun <A, Y, Z> Query<F, A, Y>.min(ord: Order<Z>, f: A.() -> Z): Query<ForId, Option<Y>, Option<Y>> =
    Query(
      select = ::identity,
      from = Id(foldable().run {
        from.foldLeft(None) { acc: Option<A>, a: A ->
          acc.fold({ Some(a) },
            { Some(if (ord.run { f(it) < f(a) }) it else a) })
        }.map(select)
      }))

  fun <Y> Query<ForId, Option<Y>, Option<Y>>.value(): Option<Y> = this@value.from.value()
}
