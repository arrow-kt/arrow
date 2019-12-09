package arrow.aql

import arrow.core.ForId
import arrow.core.identity
import arrow.core.value
import arrow.core.extensions.id.applicative.just
import arrow.typeclasses.Foldable
import arrow.typeclasses.Order

sealed class Ord<X> {
  abstract val order: Order<X>
  data class Asc<X>(override val order: Order<X>) : Ord<X>()
  data class Desc<X>(override val order: Order<X>) : Ord<X>()
}

interface OrderBy<F> {

  fun foldable(): Foldable<F>

  private data class DelegatingComparator<A>(val ord: Ord<A>) : Comparator<A> {
    override fun compare(a: A, other: A): Int = ord.order.run {
      val comp = a.compareTo(other)
      when (ord) {
        is Ord.Asc<*> -> comp
        is Ord.Desc<*> -> -comp
      }
    }
  }

  infix fun <A, Z> Query<F, A, Z>.orderBy(ord: Ord<Z>): Query<ForId, List<Z>, List<Z>> =
    foldable().run {
      Query(
        select = ::identity,
        from = from.foldLeft(emptyList<Z>()) { list, a ->
          list + select(a)
        }.sortedWith(DelegatingComparator(ord)).just()
      )
    }

  infix fun <X, Z> Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>>.orderMap(ord: Ord<X>): Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>> {
    val sortedMap = from.value().toSortedMap(kotlin.Comparator { o1, o2 ->
      val result = ord.order.run { o1.compareTo(o2) }
      when (ord) {
        is Ord.Asc<*> -> -result
        is Ord.Desc<*> -> result
      }
    })
    return Query(
      select = ::identity,
      from = sortedMap.toMap().just()
    )
  }

  fun <Z> Query<ForId, List<Z>, List<Z>>.value(): List<Z> =
    this@value.from.value()

  fun <Z, X> Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>>.value(dummy: Unit = Unit): Map<X, List<Z>> =
    this@value.from.value()
}
