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

  fun <A, Y, Z> Query<F, A, Y>.max(ord: Order<Z>, f: A.() -> Z): Query<ForId, Option<Y>, Option<Y>> =
    Query(
      select = ::identity,
      from = Id(foldable().run {
        from.foldLeft(None) { acc: Option<A>, a: A ->
          acc.fold({ Some(a) },
            { Some(if (ord.run { f(it) > f(a) }) it else a) })
        }.map(select)
      }))

  fun <A, Y> Query<ForId, Option<Y>, Option<Y>>.value(): Option<Y> = this@value.from.value()
}
