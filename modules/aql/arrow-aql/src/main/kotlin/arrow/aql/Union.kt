package arrow.aql

import arrow.core.identity
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.k
import arrow.core.extensions.list.semigroupK.combineK
import arrow.core.extensions.listk.monoid.monoid
import arrow.typeclasses.Foldable

interface Union<F> {

  fun foldable(): Foldable<F>

  infix fun <A, B, Z> Query<F, A, Z>.union(query: Query<F, B, Z>): Query<ForListK, Z, Z> =
    foldable().run {
      val la: ListK<Z> = from.foldMap(ListK.monoid()) { listOf(select(it)).k() }
      val lb: ListK<Z> = query.from.foldMap(ListK.monoid()) { listOf(query.select(it)).k() }
      val result: ListK<Z> = la.combineK(lb).k()
      Query(select = ::identity, from = result)
    }
}
